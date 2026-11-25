package com.mailook.user.controller;

import com.mailook.user.dto.CurrentUserResponse;
import com.mailook.user.service.UserService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
        return userService.getCurrentUser(jwt.getSubject(), userId.longValue());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> authenticationFailure(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(Map.of("status", "fail", "message", error.getReason()));
    }
}
