package com.mailook.warmth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 조합 하나의 보온 검사 결과.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarmthResult {
    private double outfitClo; // 입는 옷(WEAR)으로 계산한 조합 clo
    private List<MomentWarmth> moments; // 밖에 있는 시간별 결과. seq 순
    private boolean indoorOk; // 실내 검사 통과
    private boolean largeTempRange; // 시점 체감의 최고와 최저 차이가 일교차 기준 이상
}
