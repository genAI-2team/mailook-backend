package com.mailook.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.global.security.JwtProvider;
import com.mailook.user.dto.LoginReponse;
import com.mailook.user.dto.LoginRequest;
import com.mailook.user.entity.Login;
import com.mailook.user.service.Loginservice;

import lombok.extern.slf4j.Slf4j;

/**
 * [변경 후 — 2026-11-26] 로그인 소스 흐름 설명 추가. 실행 구문은 변경하지 않는다.
 *
 * 요청: frontend Login.jsx → AuthContext.login() → maiLookLoginRequest("/login", ...)
 *       → Next.js API 중계 → POST /api/auth/login
 * 처리: 요청 DTO 수신 → 서비스 인증 → 실패 예외 또는 사용자 재조회 → JWT 발급 → 성공 응답
 *
 * Controller는 HTTP 요청·응답을 담당한다. DB 조회와 BCrypt 비밀번호 검증은 서비스가 담당한다.
 * 정상적인 로그인에 성공한 경우에만 이 메소드의 마지막 HTTP 200 응답까지 도달한다.
 */
// 반환한 LoginReponse 객체를 JSON 응답 본문으로 변환한다.
@RestController
// [변경 전 — 2026-11-27] @RequestMapping("/api/maiLookLogin")
// [변경 후 — 2026-11-27] 아래 /login과 합쳐 최종 주소가 /api/auth/login이 된다.
@RequestMapping("/api/auth")
// Lombok이 log 필드를 생성한다. 아래 log.info()에서 사용한다.
@Slf4j
// [변경 전 — 2026-11-26] 파일명: loginController.java / public class loginController {
// [변경 후 — 2026-11-26] 파일명과 public 클래스명을 LoginController로 통일한다.
public class LoginController {
    // final 필드는 생성자로 한 번 주입받는다. 요청마다 새 서비스나 JWT 발급기를 만들지 않는다.
    private final Loginservice loginService;
    private final JwtProvider jwtProvider;

    // Spring이 관리하는 Loginservice와 JwtProvider Bean을 생성자 주입으로 연결한다.
    // [변경 전 — 2026-11-26] public loginController(Loginservice loginService, JwtProvider jwtProvider) {
    // [변경 후 — 2026-11-26] 생성자 이름을 변경된 클래스명에 맞춘다.
    public LoginController(Loginservice loginService, JwtProvider jwtProvider) {
        this.loginService = loginService;
        this.jwtProvider = jwtProvider;
    }

    /**
     * [변경 후 — 2026-11-26] 로그인 처리의 시작점.
     * SecurityConfig가 이 POST 경로를 공개하므로 로그인 전 JWT 없이 요청할 수 있다.
     * @RequestBody는 JSON의 email/password를 LoginRequest에 매핑한다.
     * 예: {"email":"사용자 이메일", "password":"입력 비밀번호"}
     * LoginRequest.password의 WRITE_ONLY는 입력을 허용하고 JSON 출력에서는 제외하는 설정이다.
     * 입력 JSON 자체가 잘못된 경우에는 메소드 실행 전에 변환 예외가 발생할 수 있다.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginReponse> login(@RequestBody LoginRequest loginRequest) {
        // [변경 후 — 2026-11-26] 1. 서비스에 전달할 Login 객체를 만든다.
        // LoginRequest는 외부 요청용 DTO이고, Login은 서비스·DB 조회에서 사용하는 모델이다.
        // 여기의 login은 입력값을 담는 객체이며 DB에서 조회한 회원 객체가 아니다.
        Login login = new Login();

        // 2. 입력 이메일과 비밀번호를 복사한다. 이 시점의 비밀번호는 사용자가 입력한 원문이다.
        // 이 두 줄 자체는 형식 검사나 암호화를 수행하지 않는다.
        // 이메일 trim·형식 검사는 서비스가 하고, 비밀번호는 공백을 제거하지 않고 그대로 비교한다.
        login.setEmail(loginRequest.getEmail());
        login.setPassword(loginRequest.getPassword());

        // 3. 서비스에서 이메일 검증 → 이메일로 회원 SELECT → BCrypt.matches()를 수행한다.
        // 비밀번호 일치 후 WITHDRAWN이면 탈퇴일자를 포함한 BusinessException(403)을 던진다.
        // 이 예외는 아래 result 분기를 거치지 않고 공통 GlobalExceptionHandler로 전달된다.
        // 활성 회원이면 last_login_at을 UPDATE하고 "success"를 반환한다.
        // 계정 없음·비활성·갱신 실패는 "wrongEmail", 비밀번호 오류는 "wrongPassword"를 반환한다.
        // login()은 @Transactional이므로 정상 반환 시 해당 서비스 트랜잭션이 완료된다.
        String result = loginService.login(login);

      
        // 4. 클라이언트에 보낼 응답 DTO를 준비한다. 비밀번호나 DB 해시를 담지 않는다.
        // 아래 실패 검사에서 예외가 발생하면 이 DTO는 성공 응답으로 반환되지 않는다.
        LoginReponse response = new LoginReponse();

       
        // 5. 서비스가 성공을 반환하지 않았다면 일반 인증 실패로 처리한다.
        // 상수를 앞에 놓은 equals()는 result가 null이어도 NullPointerException 없이 비교된다.
        // cause=null은 원인이 되는 별도 예외 객체가 없다는 뜻이다.
        // 공통 처리기가 INVALID_LOGIN_CREDENTIALS에 정의된 HTTP 401과 메시지를 반환한다.
        // throw 이후에는 회원 재조회·JWT 발급·HTTP 200 응답이 실행되지 않는다.
        if (!"success".equals(result)) {
            throw new BusinessException(
                    ErrorCode.INVALID_LOGIN_CREDENTIALS,
                    null);
        }
        // 6. 인증 성공 후 회원을 다시 SELECT하고 활성 상태인지 재확인한다.
        // dbLogin은 DB 조회 결과이므로 사용자 ID·닉네임 등 신뢰할 수 있는 회원 정보를 제공한다.
        // 입력 객체 login과 DB 객체 dbLogin은 서로 다르다.
        // 현재 정상 경로의 SQL 순서는 SELECT → UPDATE(last_login_at) → SELECT이다.
        Login dbLogin = loginService.getLogin(loginRequest.getEmail().trim());
        // 7. 응답 본문의 성공 표시와 안내 문구를 설정한다. 실제 HTTP 상태는 마지막 ok()에서 정한다.
        response.setStatus("success");
        response.setMessage("로그인되었습니다.");

        // 8. 응답에는 DB에서 읽은 공개 사용자 정보만 복사한다.
        // frontend는 userId/email/nickname을 Context에 저장해 헤더 등 여러 화면에서 사용한다.
        response.setUserId(dbLogin.getUserId());
        response.setEmail(dbLogin.getEmail());
        response.setNickname(dbLogin.getNickname());
        // 9. DB 회원의 email·userId를 사용하여 JwtProvider가 HS256 서명 JWT를 생성한다.
        // 서명 키는 application.properties의 jwt.secret=${JWT_SECRET} 설정을 통해 주입된다.
        // 비밀번호는 JWT에 넣지 않는다. 생성된 문자열을 응답의 accessToken에 담는다.
        // 현재 JwtProvider의 exp 설정은 주석 처리되어 있어 만료 시각 없이 발급되는 상태이다.
        response.setAccessToken(jwtProvider.createToken(dbLogin));
        
        // 10. 기존 로그 구문: 발급된 JWT 문자열을 backend 실행 로그에 직접 기록한다.
        // 이 로그 출력은 토큰 저장이나 HTTP 전송을 대신하지 않으며 실제 응답은 다음 줄에서 반환한다.
        log.info("발급 토큰:" + response.getAccessToken());
        // 11. HTTP 200과 LoginReponse JSON을 반환한다.
        // 본문: status, message, userId, email, nickname, accessToken.
        // Next.js가 응답을 중계하면 frontend API 함수가 JWT를 localStorage에 먼저 저장하고,
        // AuthContext가 사용자 상태를 갱신한 뒤 Login.jsx가 router.push("/")로 메인 이동한다.
        // backend는 화면 이동을 수행하지 않으며, 응답 전 오류 발생 시 이 줄에는 도달하지 않는다.
        return ResponseEntity.ok(response);
    }

}
