package com.mailook.user.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

/** public.users의 DB 저장 모델. 응답에는 LoginReponse만 사용한다. */
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
    private LocalDateTime locationAgreedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastLogoutAt;
    private String bodyPhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
