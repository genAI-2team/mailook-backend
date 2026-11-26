
package com.mailook.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외는 지정된 상태 코드와 메시지로 응답합니다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error("비즈니스 처리 중 서버 오류 발생: {}", errorCode.name(), exception);
        }
        // 날짜가 포함된 메시지를 HTTP 403으로 반환
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(exception.getMessage()));
    }

    // 예상하지 못한 예외는 원인을 로그에 남기고 공통 서버 오류(500번)로 응답합니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("예상하지 못한 오류 발생", exception);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getMessage()));
    }

    public record ErrorResponse(String message) {
    }
}
