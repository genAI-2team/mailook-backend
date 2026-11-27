package com.mailook.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import com.mailook.weather.dto.WeatherReading;
import com.mailook.weather.enums.WeatherKind;
import org.junit.jupiter.api.Test;

// 시연용 겨울·여름. 같은 입력이면 같은 값이다.
class MockWeatherClientTest {

    private final FeelsLikeCalculator feelsLike = new FeelsLikeCalculator();

    // 겨울 아침은 영하 체감, 낮은 아침보다 따뜻하다
    @Test
    void winter_morningIsBelowZeroAndColderThanAfternoon() {
        MockWeatherClient client = new MockWeatherClient("WINTER");
        LocalDateTime morning = LocalDateTime.of(2026, 1, 15, 8, 0);
        LocalDateTime afternoon = LocalDateTime.of(2026, 1, 15, 15, 0);

        WeatherReading early = client.read(60, 127, morning, WeatherKind.FORECAST);
        WeatherReading late = client.read(60, 127, afternoon, WeatherKind.FORECAST);

        assertTrue(early.getWindSpeed() >= 1.3);
        assertTrue(feelsLike.calc(early.getTemperature(), early.getWindSpeed(), early.getHumidity(), 1) < 0);
        assertTrue(early.getTemperature() < late.getTemperature());
        assertEquals(early, client.read(60, 127, morning, WeatherKind.FORECAST));
    }

    // 여름 낮은 습하고, 체감이 30° 안팎이다
    @Test
    void summer_afternoonIsHumidAndAroundThirty() {
        MockWeatherClient client = new MockWeatherClient("SUMMER");
        LocalDateTime afternoon = LocalDateTime.of(2026, 7, 15, 15, 0);

        WeatherReading reading = client.read(60, 127, afternoon, WeatherKind.FORECAST);
        double feels = feelsLike.calc(reading.getTemperature(), reading.getWindSpeed(), reading.getHumidity(), 7);

        assertTrue(reading.getHumidity() >= 70);
        assertTrue(feels > 28 && feels < 36);
    }

    // 관측 기온은 같은 시각 예보와 2° 넘게 차이 나지 않는다
    @Test
    void observedStaysWithinTwoDegreesOfForecast() {
        MockWeatherClient client = new MockWeatherClient("WINTER");
        LocalDateTime at = LocalDateTime.of(2026, 1, 15, 8, 0);

        WeatherReading forecast = client.read(60, 127, at, WeatherKind.FORECAST);
        WeatherReading observed = client.read(60, 127, at, WeatherKind.OBSERVED);

        assertTrue(Math.abs(observed.getTemperature() - forecast.getTemperature()) <= 2);
    }
}
