package com.mailook.user.dto;

import lombok.Data;

/** 요청한 파일명 LoginReponse를 사용하며 비밀번호와 해시는 반환하지 않는다. */
@Data
public class LoginReponse {
    private String status;
    private String message;
    private String accessToken;
    private Long userId;
    private String email;  
    private String nickname;
}
