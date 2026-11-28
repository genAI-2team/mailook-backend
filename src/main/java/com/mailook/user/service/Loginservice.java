package com.mailook.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.user.dao.LoginDAO;
import com.mailook.user.entity.Login;

@Service
public class Loginservice {
    public static final String WRONG_CREDENTIALS = "이메일 또는 비밀번호가 틀립니다.";
    private final LoginDAO loginDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Loginservice(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    /** 이메일로 사용자를 조회한 뒤 저장된 BCrypt 해시로 비밀번호를 확인한다. */
    @Transactional
    public String login(Login input) {
        String email = email(input.getEmail());
        String password = input.getPassword();
        if (password == null || password.isEmpty())
            return "wrongPassword";
        Login login = loginDAO.selectByEmail(email);
        // [변경 전 — 2026-11-27] 미가입도 일반 인증 실패로 반환했다.
        // if (login == null)
        //     return "wrongEmail";
        // [변경 후 — 2026-11-27] 조회 결과가 없는 경우에만 미가입 안내 예외를 전달한다.
        if (login == null) {
            throw new BusinessException(ErrorCode.USER_NOT_REGISTERED, null);
        }
        if (!passwordEncoder.matches(password, login.getPassword()))
            return "wrongPassword";
        if ("WITHDRAWN".equalsIgnoreCase(login.getStatus())) {
            String message = ErrorCode.USER_WITHDRAWN.getMessage();
            if (login.getWithdrawnAt() != null) {
                message += " (탈퇴일자: " + login.getWithdrawnAt().toLocalDate() + ")";
            }
            throw new BusinessException(ErrorCode.USER_WITHDRAWN, message, null);
        }
        if (!"ACTIVE".equals(login.getStatus()))
            return "wrongEmail";
        // 마지막 로그인 날짜 업데이트 처리
        if (loginDAO.updateLastLogin(login.getUserId()) != 1)
            return "wrongEmail";
        return "success";
    }

    public Login getLogin(String authenticatedEmail) {
        if (authenticatedEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "미 가입회원입니다. 계정을 등록 하시기 바랍니다..");
        }
        Login login = loginDAO.selectByEmail(authenticatedEmail);
        if (login == null || !"ACTIVE".equals(login.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "활동 계정이 아닙니다.");
        }
        return login;
    }

    private String email(String value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, null);
        }

        String email = value.trim();

        if (email.length() > 50
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, null);
        }

        return email;
    }
}
