package com.mailook.global.exception.example;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 임시 학습 예시: exception-example 프로필에서만 활성화하며, 나중에 이 파일을 삭제하면 됩니다.
@Profile("exception-example")
@RestController
@RequestMapping("/examples/exceptions")
public class ExceptionExampleController {

    // ~~~~ 오류 코드만 전달하는 예시: HTTP 404 ~~~~
    @GetMapping("/clothes-not-found")
    public void clothesNotFound() {
        // 옷을 찾지 못한 상황을 가정합니다. 예외는 GlobalExceptionHandler가 처리합니다.
        throw new BusinessException(ErrorCode.CLOTHES_NOT_FOUND);
    }

    // ~~~~ 실제 원인 예외도 함께 전달하는 예시: HTTP 500 ~~~~
    @GetMapping("/save-failed")
    public void saveFailed() {
        try {
            // 실제 DB 작업 대신 저장 실패를 의도적으로 발생시킵니다.
            throw new IllegalStateException("예시: DB 저장 실패");
        } catch (IllegalStateException e) {
            throw new BusinessException(ErrorCode.CLOTHES_CREATE_FAILED, e);
        }
    }
}
