package com.mailook.recommend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mailook.global.enums.Situation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * recommendations 테이블에 대응하는 추천 결과.
 * 같은 날 다시 추천하면 기존 결과를 덮어쓰지 않고 새 행으로 저장한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class Recommendation {

    private Long recommendationId;
    private Long userId;

    // 추천 대상 날짜. 같은 사용자와 날짜에 여러 버전이 존재할 수 있음
    private LocalDate recommendDate;

    // 오늘의 상황 (실내, 야외, 이동, 격식 있는 자리)을 저장하는 필드
    private Situation situation;

    // LLM 추천 이유. 규칙 기반 추천이면 안내 문구
    private String reason;

    // 추천 당시 "오늘은 몸이 안 좋아요" 설정값
    private boolean unwell;

    private LocalDateTime createdAt;
}
