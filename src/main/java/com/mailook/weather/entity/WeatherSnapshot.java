package com.mailook.weather.entity;

import java.time.LocalDateTime;
import com.mailook.weather.enums.WeatherKind;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** weather_snapshots 한 줄. */
@Getter
@Setter
@NoArgsConstructor
public class WeatherSnapshot {
    private Long weatherSnapshotId;
    private int nx;
    private int ny;
    private LocalDateTime forecastAt;
    private WeatherKind kind;
    private LocalDateTime baseAt;
    private double temperature;
    private double feelsLike;
    private double windSpeed;
    private int humidity;
    private String precipitationType;
    private String sky;
    private LocalDateTime createdAt;
}
