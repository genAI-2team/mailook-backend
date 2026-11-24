package com.mailook.user.dto;

import lombok.Data;

/** 회원가입 결과. 비밀번호는 응답에 포함하지 않는다. */
@Data
public class SignupReponse {
    private String status;
    private String code;
    private String message;
    private Long userId;
    private String email;
    private String nickname;
}
  