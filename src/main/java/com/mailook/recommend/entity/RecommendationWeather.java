package com.mailook.recommend.entity;

import java.math.BigDecimal;

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
    private BigDecimal requiredClo;
    // numeric(3,2) 전체 숫자 3자리 중 소수점 아래 2자리를 저장한다는 뜻입니다.
    /*
     * 1.25 정상 저장
     * 1.256 소수 둘째 자리로 반올림되어 1.26 저장
     * 10.00 범위를 초과하여 오류
     */

    // 옷 선택의 기준 시점 여부 (requiredClo가 가장 높은 시점)
    private boolean isDeciding;
}
