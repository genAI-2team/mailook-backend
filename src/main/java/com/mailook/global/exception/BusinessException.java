package com.mailook.global.exception;

import lombok.Getter;

// 비즈니스 로직에서 발생한 오류의 코드와 메시지를 전달하는 예외입니다.
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    // ~~~~ 오류 코드만 전달하는 경우 ~~~~
    // 비즈니스 조건을 충족하지 못했을 때 오류 코드로 예외를 생성합니다.
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // ~~~~ 오류 코드와 실제 오류 원인을 함께 전달하는 경우 ~~~~
    // 오류 코드로 예외를 생성하고, 실제 오류 원인도 함께 저장합니다.
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    // ~~~~ 서비스 코드에서 사용하는 try-catch 예시 ~~~~
    /*
    try {
        // 여기에 DB 저장 등 오류가 발생할 수 있는 실제 처리 코드를 작성합니다.
    } catch (Exception e) {
        // 위 생성자를 호출하여 서비스 오류 코드와 실제 원인 e를 함께 전달합니다.
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e);
    }
    */
}