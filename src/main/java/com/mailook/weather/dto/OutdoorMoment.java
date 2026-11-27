package com.mailook.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 그날 바깥 시점 하나. 보온 계산에 넘기는 값만 담는다.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutdoorMoment {
    private int seq; // 그날 바깥 시점 순서(1부터)
    private double feelsLike; // 그 시점 체감온도(°)
}
