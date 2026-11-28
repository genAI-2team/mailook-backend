package com.mailook.user.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.mailook.user.dto.SignupRequest;
import com.mailook.user.dto.SignupReponse;
import com.mailook.user.entity.Signup;
import com.mailook.user.service.SignupService;
import com.mailook.global.security.JwtProvider;
import com.mailook.user.entity.Login;

import lombok.extern.slf4j.Slf4j;

@RestController
// [변경 전 — 2026-11-27] @RequestMapping("/api/maiLookLogin")
// [변경 후 — 2026-11-27] 메소드의 /signup과 합쳐 /api/auth/signup으로 등록한다.
@RequestMapping("/api/auth")
@Slf4j 
public class SignupController {
    private final SignupService signupService;
    private final JwtProvider jwtProvider;

    // [변경 전 — 2026-11-27] public SignupController(SignupService signupService) {
    // [변경 후 — 2026-11-27] 가입 완료 회원의 인증 토큰도 발급한다.
    public SignupController(SignupService signupService, JwtProvider jwtProvider) {
        this.signupService = signupService;
        this.jwtProvider = jwtProvider;
    }

    // [변경 전 — 2026-11-27] @PostMapping("/insert")
    // [변경 후 — 2026-11-27] POST /api/auth/signup. 내부 insert 메소드와 등록 로직은 유지한다.
    @PostMapping("/signup")
    public ResponseEntity<SignupReponse> insert(@RequestBody SignupRequest request) {
        Signup signup = new Signup();
        signup.setEmail(request.getEmail());
        signup.setPassword(request.getPassword());
        signup.setNickname(request.getNickname());
        boolean registered = signupService.insert(signup, request.getLocationConsent());
        if (!registered) {
            SignupReponse duplicate = new SignupReponse();
            duplicate.setStatus("fail");
            duplicate.setCode("DUPLICATE_EMAIL");
            duplicate.setMessage("이미 사용 중인 이메일입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicate);
        }
        SignupReponse response = new SignupReponse();
        response.setStatus("success");
        response.setMessage("회원가입이 완료되었습니다.");
        response.setUserId(signup.getUserId());
        response.setEmail(signup.getEmail());
        response.setNickname(signup.getNickname());
        // [변경 전 — 2026-11-27] 회원 정보만 반환하고 기본 정보 화면용 인증은 발급하지 않았다.
        // [변경 후 — 2026-11-27] DB 등록 성공 후 생성된 userId/email로 토큰을 발급한다.
        Login registeredUser = new Login();
        registeredUser.setUserId(signup.getUserId());
        registeredUser.setEmail(signup.getEmail());
        response.setAccessToken(jwtProvider.createToken(registeredUser));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<SignupReponse> fail(ResponseStatusException error) {
        return failure(error.getStatusCode().value(), error.getReason());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SignupReponse> unreadable() {
        return failure(400, "요청 내용을 확인해 주세요.");
    }

    @ExceptionHandler({DataAccessException.class, org.springframework.transaction.CannotCreateTransactionException.class})
    public ResponseEntity<SignupReponse> databaseFailure() {
        return failure(500, "회원가입 등록에 실패했습니다. 잠시 후 다시 시도해 주세요.");
    }

    private ResponseEntity<SignupReponse> failure(int status, String message) {
        SignupReponse response = new SignupReponse();
        response.setStatus("fail");
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }
}
