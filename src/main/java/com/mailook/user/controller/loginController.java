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

@RestController
@RequestMapping("/api/maiLookLogin")
@Slf4j
public class loginController {
    private final Loginservice loginService;
    private final JwtProvider jwtProvider;

    public loginController(Loginservice loginService, JwtProvider jwtProvider) {
        this.loginService = loginService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginReponse> login(@RequestBody LoginRequest loginRequest) {
        // 요청 DTO를 엔티티로 변환한다.
        Login login = new Login();

        login.setEmail(loginRequest.getEmail());
        login.setPassword(loginRequest.getPassword());

        String result = loginService.login(login);

        // 응답 생성은 MemberController처럼 Controller에서 담당한다.
        LoginReponse response = new LoginReponse();

       
        if (!"success".equals(result)) {
            throw new BusinessException(
                    ErrorCode.INVALID_LOGIN_CREDENTIALS,
                    null);
        }
        Login dbLogin = loginService.getLogin(loginRequest.getEmail().trim());
        response.setStatus("success");
        response.setMessage("로그인되었습니다.");

        response.setUserId(dbLogin.getUserId());
        response.setEmail(dbLogin.getEmail());
        response.setNickname(dbLogin.getNickname());
        response.setAccessToken(jwtProvider.createToken(dbLogin));
        
        log.info("발급 토큰:" + response.getAccessToken());
        return ResponseEntity.ok(response);
    }

}
