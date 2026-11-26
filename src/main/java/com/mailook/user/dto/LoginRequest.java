package com.mailook.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequest {
    private String email;
    // WRITE_ONLY를 사용하더라도 Java 내부에서는 getPassword()로 비밀번호를 조회, JSON 응답을 제외 처리
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String nickname;
    private Boolean locationConsent;
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword; // 현재 사용 중인 비밀번호를 저장하는 변수 : 비밀번호 변경, 회원 탈퇴, 중요 회원정보 변경 시 본인 확인 목적으로 사용
}
  