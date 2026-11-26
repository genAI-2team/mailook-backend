package com.mailook.user.service;

import com.mailook.user.dao.LoginDAO;
import com.mailook.user.dto.CurrentUserResponse;
import com.mailook.user.entity.Login;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final LoginDAO loginDAO;

    public UserService(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    /** Spring Security가 검증한 JWT의 사용자만 조회한다. 비밀번호는 반환하지 않는다. */
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String email, Long userId) {
        if (email == null || userId == null) throw unauthorized();
        Login user = loginDAO.selectByEmail(email);
        if (user == null || !"ACTIVE".equals(user.getStatus()) || !userId.equals(user.getUserId())) {
            throw unauthorized();
        }
        return new CurrentUserResponse(user.getUserId(), user.getEmail(), user.getNickname());
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 유효하지 않습니다. 다시 로그인해 주세요.");
    }
}
