package com.mailook.user.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = loginController.class)
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
