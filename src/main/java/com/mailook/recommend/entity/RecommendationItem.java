package com.mailook.recommend.entity;

import com.mailook.recommend.enums.ItemRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * recommendation_items 테이블에 대응하는 추천 의류.
 * recommendationId와 clothesId가 복합 기본 키를 구성한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class RecommendationItem {

    private Long recommendationId;
    private Long clothesId;

    // WEAR: 입는 옷, BRING: 챙겨 가면 좋은 것
    private ItemRole role = ItemRole.WEAR;

    // BRING 항목을 챙기면 좋은 이유 (LLM 작성, 최대 255자). null 허용
    private String reason;
    // LLM이 만든 이유가 225자가 넘어가면 오류가 날 수도 있음
}
