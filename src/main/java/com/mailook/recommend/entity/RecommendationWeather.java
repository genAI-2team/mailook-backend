package com.mailook.recommend.entity;

import com.mailook.global.enums.WeatherMoment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * recommendation_weathers 테이블에 대응하는 추천 시점별 날씨 정보.
 * recommendationId와 seq가 복합 기본 키를 구성한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class RecommendationWeather {

    private Long recommendationId;

    // 그날 동선 순서 (1부터)
    private Integer seq;

    // 출발, 이동, 야외 체류, 귀가 또는 일정 없는 날의 시점
    private WeatherMoment moment;

    // 날씨를 조회한 장소 ID
    private Long placeId;

    // 추천에 사용한 예보(FORECAST) 스냅샷 ID
    private Long weatherSnapshotId;

    // 해당 시점에 필요한 조합 보온력
    private double requiredClo;
    // DB에서는 numeric(3,2)로 소수 둘째 자리까지 저장한다.
    // 저장 시 1.256은 1.26으로 반올림되며, 결과가 -9.99~9.99 범위를 벗어나면 오류가 발생한다.

    // 옷 선택의 기준 시점 여부 (requiredClo가 가장 높은 시점)
    private boolean isDeciding;
}
