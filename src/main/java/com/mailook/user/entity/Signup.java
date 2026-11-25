package com.mailook.user.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

/** public.users에 최초 등록할 회원 정보. */
@Data
public class Signup {
    private Long userId;
    private String email;
    @JsonIgnore
    @ToString.Exclude
    private String password;
    private String nickname;
    private LocalDateTime locationAgreedAt;
}
