package com.mailook.user.dto;

/** 로그인 복원에 필요한 사용자 정보만 반환한다. */
public record CurrentUserResponse(Long userId, String email, String nickname) {
}
