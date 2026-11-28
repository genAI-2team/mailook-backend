package com.mailook.user.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// [변경 전 — 2026-11-26] @RestControllerAdvice(assignableTypes = loginController.class)
// [변경 후 — 2026-11-26] 이름을 변경한 로그인 Controller에 예외 처리를 연결한다.
@RestControllerAdvice(assignableTypes = LoginController.class)
public class LoginExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handle(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(Map.of("status", "fail", "message", error.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> unreadable() {
        return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", "요청 내용을 확인해 주세요."));
    }
}
