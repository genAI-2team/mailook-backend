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

@RestController
@RequestMapping("/api/maiLookLogin")
public class SignupController {
    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    /** 기존 Next.js insert 중계 경로와 공개 회원가입 경로를 사용한다. */
    @PostMapping("/insert")
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
