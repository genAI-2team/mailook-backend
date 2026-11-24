package com.mailook.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ※ 기본 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "로그인이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_005", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_006", "서버 오류가 발생했습니다."),
    DATA_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_007", "저장된 내용 삭제에 실패했습니다."),
    DATA_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_008", "기존 내용 수정에 실패했습니다."),

    // ※ 비즈니스 오류
    CLOTHES_NOT_FOUND(HttpStatus.NOT_FOUND, "CLOTHES_001", "의류를 찾을 수 없습니다."),
    CLOTHES_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CLOTHES_002", "옷 등록에 실패했습니다."),
    CLOTHES_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CLOTHES_003", "저장된 옷 데이터를 불러오지 못했습니다."),
    STORAGE_LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "STORAGE_001", "보관 위치를 찾을 수 없습니다."),
    WEAR_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "WEAR_001", "착용 기록을 찾을 수 없습니다."),
    WEAR_RECORD_ALREADY_EXISTS(HttpStatus.CONFLICT, "WEAR_002", "해당 날짜의 착용 기록이 이미 존재합니다."),
    WEAR_RECORD_LOCKED(HttpStatus.CONFLICT, "WEAR_003", "피드백이 등록된 착용 기록은 변경할 수 없습니다."),

    PLACE_SCHEDULE_TIME_CONFLICT(HttpStatus.BAD_REQUEST, "PLACE_001", "도착 시간과 떠나는 시간이 같습니다."),
    PLACE_LOCATION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PLACE_002", "위치 저장에 실패했습니다."),

    // 로그인 ID 혹은 PW가 올바르지 않을 때
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "이메일과 패스워드를 다시 확인해주세요."),

    // ※ AI 오류 모음
    AI_WEATHER_RECOMMENDATION_FAILED(HttpStatus.BAD_GATEWAY, "AI_001", "AI가 날씨 정보와 추천을 생성하지 못했습니다.");
    
    // AI 오류 모음 中 세부사항
        //생성한 추천 결과를 DB에 저장하지 못 함
    // AI_RECOMMENDATION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_002", "추천 결과가 저장되지 않았습니다."), 
    //     // 옷 데이터가 하나도 저장되어 있지 않을 때 (AI가 추천할 수 없는 상황)
    // AI_INSUFFICIENT_CLOTHES(HttpStatus.UNPROCESSABLE_ENTITY, "AI_003", "저장된 옷이 없습니다."),
    //     // 추천은 되지만 저장된 옷 중 아주 딱 맞을 정도로 적절한 옷이 없을 때
    // AI_NO_MATCHING_CLOTHES(HttpStatus.UNPROCESSABLE_ENTITY, "AI_004", "오늘 기온에 맞는 적절한 옷이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
