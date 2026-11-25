package com.mailook.user.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import com.mailook.user.dto.Pager;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
        if (login == null || !"ACTIVE".equals(login.getStatus())) return "wrongEmail";
        if (!passwordEncoder.matches(password, login.getPassword())) return "wrongPassword";
        // 마지막 로그인 날짜 업데이트 처리
        if (loginDAO.updateLastLogin(login.getUserId()) != 1) return "wrongEmail";
        return "success";
    }

    @Transactional
    public void insert(Login login, Boolean locationConsent) {
        login.setEmail(email(login.getEmail()));
        validateNewPassword(login.getPassword());
        login.setNickname(nickname(login.getNickname()));
        if (!Boolean.TRUE.equals(locationConsent)) badRequest("위치 정보 이용 동의가 필요합니다.");
        login.setLocationAgreedAt(LocalDateTime.now());
        try {
            loginDAO.insert(login);
            loginDAO.insertUserSettings(login.getUserId());
            loginDAO.insertPreferences(login.getUserId());
        } catch (DuplicateKeyException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "사용할 수 없는 이메일입니다.");
        }
    }

    public Login selectById(String authenticatedEmail, Long userId) {
        Login login = requireUser(authenticatedEmail);
        requireOwner(login, userId);
        return login;
    }

    public Login getLogin(String authenticatedEmail) {
        return requireUser(authenticatedEmail);
    }

    public int selectCount(String authenticatedEmail) {
        Map<String, Object> where = Map.of("userId", requireUser(authenticatedEmail).getUserId());
        return loginDAO.selectCount(where);
    }

    public List<Login> selectPage(String authenticatedEmail, int pageNo, int size) {
        if (pageNo < 1 || size < 1 || size > 100 || pageNo > Integer.MAX_VALUE / size) badRequest("페이지 번호와 크기를 확인해 주세요.");
        Long userId = requireUser(authenticatedEmail).getUserId();
        Map<String, Object> where = Map.of("userId", userId);
        Pager pager = new Pager(size, 5, loginDAO.selectCount(where), pageNo);
        return loginDAO.selectPage(pager, where);
    }

    @Transactional
    public int update(String authenticatedEmail, Login input, String currentPassword) {
        Login login = requireUser(authenticatedEmail);
        requireOwner(login, input.getUserId());
        verifyCurrentPassword(currentPassword, login);
        if (input.getEmail() != null) login.setEmail(email(input.getEmail()));
        if (input.getNickname() != null) login.setNickname(nickname(input.getNickname()));
        if (input.getPassword() != null) {
            validateNewPassword(input.getPassword());
            login.setPassword(input.getPassword());
        }
        try {
            return loginDAO.update(login);
        } catch (DuplicateKeyException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "사용할 수 없는 이메일입니다.");
        }
    }

    @Transactional
    public int deleteById(String authenticatedEmail, Long userId, String currentPassword) {
        Login login = requireUser(authenticatedEmail);
        requireOwner(login, userId);
        verifyCurrentPassword(currentPassword, login);
        return loginDAO.deleteById(userId);
    }

    @Transactional
    public void logout(String authenticatedEmail) {
        loginDAO.updateLastLogout(requireUser(authenticatedEmail).getUserId());
    }

    private Login requireUser(String email) {
        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        Login login = loginDAO.selectByEmail(email);
        if (login == null || !"ACTIVE".equals(login.getStatus())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        return login;
    }

    private void requireOwner(Login login, Long userId) {
        if (!login.getUserId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 정보만 처리할 수 있습니다.");
    }

    private void verifyCurrentPassword(String password, Login login) {
        if (password == null || password.isEmpty() || !password.equals(login.getPassword())) wrongCredentials();
    }

    private String email(String value) {
        if (value == null || value.trim().length() > 100 || !value.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) badRequest("올바른 이메일을 입력해 주세요.");
        return value.trim();
    }

    private String nickname(String value) {
        if (value == null || value.isBlank() || value.trim().length() > 100) badRequest("닉네임을 1~100자로 입력해 주세요.");
        return value.trim();
    }

    private void validateNewPassword(String value) {
        if (value == null || !value.matches("^(?=[A-Za-z0-9]{8,20}$)(?=.*[0-9])[A-Za-z][A-Za-z0-9]*$")) {
            badRequest("비밀번호는 영문으로 시작하는 영문·숫자 조합 8~20자로 입력해 주세요.");
        }
    }

    private void wrongCredentials() { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, WRONG_CREDENTIALS); }
    private void badRequest(String message) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
