package com.mailook.weather.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.mailook.weather.entity.WeatherSnapshot;
import com.mailook.weather.enums.WeatherKind;
import com.mailook.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 오늘 예보 스냅샷. 홈에서 바깥 시간 날씨를 볼 때 쓴다.
// 동선별 시각은 아직 없어서, 확인용으로 8시·15시·18시를 저장한다.
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final int[] HOURS = {8, 15, 18};

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/today")
    public List<WeatherSnapshot> today(
            @RequestParam(defaultValue = "60") int nx,
            @RequestParam(defaultValue = "127") int ny) {
        LocalDateTime now = LocalDateTime.now(SEOUL);
        List<WeatherSnapshot> snapshots = new ArrayList<>();
        for (int hour : HOURS) {
            LocalDateTime forecastAt = now.toLocalDate().atTime(hour, 0);
            snapshots.add(weatherService.getSnapshot(nx, ny, forecastAt, WeatherKind.FORECAST, now));
        }
        return snapshots;
    }
}
