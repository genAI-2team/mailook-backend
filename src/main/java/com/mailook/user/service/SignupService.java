package com.mailook.user.service;


import java.time.LocalDateTime;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mailook.user.dao.SignupDAO;
import com.mailook.user.entity.Signup;

@Service
public class SignupService {
    private final SignupDAO signupDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SignupService(SignupDAO signupDAO) {
        this.signupDAO = signupDAO;
    }

    @Transactional
    public boolean insert(Signup signup, Boolean locationConsent) {
        String email = signup.getEmail();
        if (email == null || email.length() > 50 || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 이메일을 50자 이내로 입력해 주세요.");
        if (signup.getPassword() == null || !signup.getPassword().matches("^(?=[A-Za-z0-9]{2,20}$)(?=.*[0-9])[A-Za-z][A-Za-z0-9]*$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 영문자로 시작하는 영문·숫자 조합 20자 이내로 입력해 주세요.");
        if (signup.getNickname() == null || !signup.getNickname().matches("^[가-힣ㄱ-ㅎㅏ-ㅣA-Za-z0-9]{1,50}$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임은 한글·영문·숫자 50자 이내로 입력해 주세요.");
        if (!Boolean.TRUE.equals(locationConsent))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "위치 정보 이용에 동의해 주세요.");
        signup.setEmail(email.trim());
        if (signupDAO.existsByEmail(signup.getEmail())) {
            return false;
        }
        signup.setPassword(passwordEncoder.encode(signup.getPassword()));
        signup.setLocationAgreedAt(LocalDateTime.now());
        try {
            if (signupDAO.insert(signup) != 1)
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 등록에 실패했습니다.");
        } catch (DuplicateKeyException error) {
            // 사전 조회 직후 같은 이메일로 동시에 가입하더라도 UNIQUE 제약으로 차단한다.
            if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
                org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return false;
        }
        return true;
    }
}
