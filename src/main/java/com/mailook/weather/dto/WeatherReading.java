package com.mailook.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 한 격자·시각의 날씨. 체감온도는 여기 넣지 않고 FeelsLikeCalculator가 만든다.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReading {
    private double temperature; // 기온(°C)
    private double windSpeed; // 풍속(m/s)
    private int humidity; // 상대습도(%)
    private String precipitationType; // 기상청 PTY. 0 없음
    private String sky; // 기상청 SKY. 1 맑음 / 3 구름많음 / 4 흐림
}
