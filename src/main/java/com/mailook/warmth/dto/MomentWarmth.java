package com.mailook.warmth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 밖에 있는 시간 하나의 보온 결과.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentWarmth {
    private int seq; // OutdoorMoment.seq 와 같다
    private double requiredClo; // 그 시점에 필요한 조합 clo
    private double diff; // 조합 clo − 적정 clo. +면 따뜻한 쪽, −면 추운 쪽
    private boolean deciding; // 그날 기준 시점. 하루 1개
}
