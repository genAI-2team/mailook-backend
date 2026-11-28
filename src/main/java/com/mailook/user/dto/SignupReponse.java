package com.mailook.user.dto;

import lombok.Data;
import lombok.ToString;

/** 회원가입 결과. 비밀번호는 응답에 포함하지 않는다. */
@Data
public class SignupReponse {
    private String status;
    private String code;
    private String message;
    private Long userId;
    private String email;
    private String nickname;
    // [신규 — 2026-11-27] 가입 후 기본 정보 등록에 사용할 인증 토큰. 로그에서는 제외한다.
    @ToString.Exclude
    private String accessToken;
}
