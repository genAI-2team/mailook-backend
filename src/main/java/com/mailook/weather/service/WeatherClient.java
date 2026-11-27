package com.mailook.weather.service;

import java.time.LocalDateTime;
import com.mailook.weather.dto.WeatherReading;
import com.mailook.weather.enums.WeatherKind;

// 격자·시각의 기온, 바람, 습도, 하늘, 강수를 가져온다. 기상청과 모의가 같은 모양이다.
public interface WeatherClient {

    WeatherReading read(int nx, int ny, LocalDateTime forecastAt, WeatherKind kind);
}
