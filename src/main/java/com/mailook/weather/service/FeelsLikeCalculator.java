package com.mailook.weather.service;

import org.springframework.stereotype.Component;

// 체감온도 계산. 기상청 식을 그대로 쓴다.
// 5~9월은 기온·습도, 10~4월은 기온·바람. 사람 보정은 넣지 않는다.
// temperature: 기온(°C), windSpeed: 풍속(m/s), humidity: 상대습도(%), month: 1~12
@Component
public class FeelsLikeCalculator {

    // 겨울 체감은 기온 10°C 이하, 풍속 1.3m/s 이상일 때만 산출한다.
    private static final double WINTER_MAX_TEMP = 10;
    private static final double WINTER_MIN_WIND = 1.3;
    // 예보 풍속은 m/s, 겨울 식의 풍속은 km/h
    private static final double MPS_TO_KMH = 3.6;

    public double calc(double temperature, double windSpeed, double humidity, int month) {
        if (month >= 5 && month <= 9) {
            return summer(temperature, humidity);
        }
        return winter(temperature, windSpeed);
    }

    // 체감온도 = -0.2442 + 0.55399Tw + 0.45535Ta - 0.0022Tw² + 0.00278Tw×Ta + 3.0
    // Tw는 Stull 추정식으로 구한 습구온도
    private double summer(double temperature, double humidity) {
        double wetBulb = wetBulb(temperature, humidity);
        return -0.2442
                + 0.55399 * wetBulb
                + 0.45535 * temperature
                - 0.0022 * wetBulb * wetBulb
                + 0.00278 * wetBulb * temperature
                + 3.0;
    }

    // Tw = Ta×atan[0.151977×√(RH+8.313659)] + atan(Ta+RH) - atan(RH-1.67633)
    //      + 0.00391838×RH^1.5×atan(0.023101×RH) - 4.686035
    private double wetBulb(double temperature, double humidity) {
        return temperature * Math.atan(0.151977 * Math.sqrt(humidity + 8.313659))
                + Math.atan(temperature + humidity)
                - Math.atan(humidity - 1.67633)
                + 0.00391838 * Math.pow(humidity, 1.5) * Math.atan(0.023101 * humidity)
                - 4.686035;
    }

    // 체감온도 = 13.12 + 0.6215Ta - 11.37×V^0.16 + 0.3965×V^0.16×Ta
    // V는 풍속(km/h). 조건을 못 채우면 기온을 그대로 쓴다.
    private double winter(double temperature, double windSpeed) {
        if (temperature > WINTER_MAX_TEMP || windSpeed < WINTER_MIN_WIND) {
            return temperature;
        }
        double windKmh = windSpeed * MPS_TO_KMH;
        double windFactor = Math.pow(windKmh, 0.16);
        return 13.12 + 0.6215 * temperature - 11.37 * windFactor + 0.3965 * windFactor * temperature;
    }
}
