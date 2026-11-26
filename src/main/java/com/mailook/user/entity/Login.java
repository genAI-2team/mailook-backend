package com.mailook.user.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

/** 로그인과 사용자 인증 확인에 필요한 public.users 조회 모델. */
@Data
public class Login {
    private Long userId;
    private String email;
    @JsonIgnore
    @ToString.Exclude
    private String password;
    private String nickname;
    private String status;
    private LocalDateTime withdrawnAt;
}
