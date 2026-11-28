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
    @JsonIgnore // JSON 데이터를 주고받을 때 이 필드는 제외한다.
    @ToString.Exclude // 객체를 문자열로 출력할 때 이 필드는 제외한다.
    private String password;
    private String nickname;
    private LocalDateTime locationAgreedAt;
}
