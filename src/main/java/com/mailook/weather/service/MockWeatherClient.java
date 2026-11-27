package com.mailook.weather.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.mailook.weather.dto.WeatherReading;
import com.mailook.weather.enums.WeatherKind;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// 시연용 날씨. WINTER는 아침 체감이 영하이고 바람이 세다. SUMMER는 체감 30° 안팎, 습도가 높다.
// 날짜·격자·시각이 같으면 항상 같은 값이다. 관측은 같은 시각 예보에서 ±2° 안이다.
@Component
@ConditionalOnProperty(name = "weather.client", havingValue = "mock")
public class MockWeatherClient implements WeatherClient {

    private final String scenario;

    public MockWeatherClient(@Value("${weather.mock.scenario}") String scenario) {
        this.scenario = scenario;
    }

    @Override
    public WeatherReading read(int nx, int ny, LocalDateTime forecastAt, WeatherKind kind) {
        LocalDate date = forecastAt.toLocalDate();
        int hour = forecastAt.getHour();
        double curve = dayCurve(hour);
        double jitter = jitter(date, nx, ny, hour);
        boolean winter = "WINTER".equals(scenario);

        double temperature = winter ? -4 + curve * 10 + jitter : 26 + curve * 8 + jitter;
        if (kind == WeatherKind.OBSERVED) {
            temperature += observedShift(date, nx, ny, hour);
        }

        if (winter) {
            return new WeatherReading(temperature, 6, 50, "0", "4");
        }
        if ("SUMMER".equals(scenario)) {
            return new WeatherReading(temperature, 2, 75, "0", "1");
        }
        throw new IllegalArgumentException("weather.mock.scenario는 WINTER 또는 SUMMER입니다.");
    }

    // 6시가 가장 춥고 15시 전후가 가장 덥다.
    private double dayCurve(int hour) {
        return 0.5 - 0.5 * Math.cos(2 * Math.PI * (hour - 6) / 24.0);
    }

    // -0.5° ~ 0.5°. 격자·날짜마다 조금씩만 다르다.
    private double jitter(LocalDate date, int nx, int ny, int hour) {
        int mixed = mix(date, nx, ny, hour);
        return (Math.floorMod(mixed, 11) - 5) / 10.0;
    }

    // 예보와의 차이. -2° ~ 2°.
    private double observedShift(LocalDate date, int nx, int ny, int hour) {
        return Math.floorMod(mix(date, nx, ny, hour) + 3, 5) - 2;
    }

    private int mix(LocalDate date, int nx, int ny, int hour) {
        int day = date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
        return day * 31 + nx * 17 + ny * 13 + hour * 7;
    }
}
