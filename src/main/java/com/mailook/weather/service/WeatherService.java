package com.mailook.weather.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import com.mailook.weather.dao.WeatherSnapshotDao;
import com.mailook.weather.dto.WeatherReading;
import com.mailook.weather.entity.WeatherSnapshot;
import com.mailook.weather.enums.WeatherKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 격자·시각 날씨를 스냅샷으로 남긴다. 숫자는 WeatherClient가 가져온다.
// weather.client=mock 이면 시연 값, kma 이면 기상청이다. 같은 발표면 있는 줄을 쓰고, 값이 같으면 새 줄을 만들지 않는다.
@Service
public class WeatherService {

    private static final int[] FORECAST_HOURS = {2, 5, 8, 11, 14, 17, 20, 23};

    private final WeatherSnapshotDao snapshotDao;
    private final WeatherClient weatherClient;
    private final FeelsLikeCalculator feelsLikeCalculator;

    public WeatherService(WeatherSnapshotDao snapshotDao,
            @Autowired(required = false) WeatherClient weatherClient,
            FeelsLikeCalculator feelsLikeCalculator) {
        this.snapshotDao = snapshotDao;
        this.weatherClient = weatherClient;
        this.feelsLikeCalculator = feelsLikeCalculator;
    }

    // now 기준의 최신 발표로 그 격자·시각 날씨를 가져온다.
    public WeatherSnapshot getSnapshot(int nx, int ny, LocalDateTime forecastAt, WeatherKind kind, LocalDateTime now) {
        LocalDateTime baseAt = baseAt(kind, now);
        WeatherSnapshot latest = snapshotDao.selectLatest(nx, ny, forecastAt, kind);
        if (latest != null && baseAt.equals(latest.getBaseAt())) {
            return latest;
        }

        if (weatherClient == null) {
            throw new IllegalStateException("weather.client=kma 인데 기상청 호출은 아직 연결하지 않았습니다.");
        }
        WeatherReading reading = weatherClient.read(nx, ny, forecastAt, kind);
        double temperature = reading.getTemperature();
        double windSpeed = reading.getWindSpeed();
        int humidity = reading.getHumidity();
        double feelsLike = feelsLikeCalculator.calc(
                temperature, windSpeed, humidity, forecastAt.getMonthValue());

        WeatherSnapshot created = new WeatherSnapshot();
        created.setNx(nx);
        created.setNy(ny);
        created.setForecastAt(forecastAt);
        created.setKind(kind);
        created.setBaseAt(baseAt);
        created.setTemperature(temperature);
        created.setFeelsLike(feelsLike);
        created.setWindSpeed(windSpeed);
        created.setHumidity(humidity);
        created.setPrecipitationType(reading.getPrecipitationType());
        created.setSky(reading.getSky());

        if (latest != null && sameWeather(latest, created)) {
            return latest;
        }
        snapshotDao.insert(created);
        return created;
    }

    // 예보 발표는 02·05·08·11·14·17·20·23시, 10분 뒤부터 쓸 수 있다.
    // 관측은 매시 정각 값이고, 40분부터 쓸 수 있다.
    static LocalDateTime baseAt(WeatherKind kind, LocalDateTime now) {
        LocalDateTime trimmed = now.withSecond(0).withNano(0);
        if (kind == WeatherKind.OBSERVED) {
            LocalDateTime hour = trimmed.withMinute(0);
            if (trimmed.getMinute() < 40) {
                return hour.minusHours(1);
            }
            return hour;
        }

        LocalDateTime available = trimmed.minusMinutes(10);
        int minuteOfDay = available.getHour() * 60 + available.getMinute();
        int chosen = -1;
        for (int hour : FORECAST_HOURS) {
            if (hour * 60 <= minuteOfDay) {
                chosen = hour;
            }
        }
        LocalDate date = available.toLocalDate();
        if (chosen < 0) {
            return date.minusDays(1).atTime(23, 0);
        }
        return date.atTime(chosen, 0);
    }

    private boolean sameWeather(WeatherSnapshot saved, WeatherSnapshot next) {
        if (saved.getTemperature() != next.getTemperature()) return false;
        if (saved.getFeelsLike() != next.getFeelsLike()) return false;
        if (saved.getWindSpeed() != next.getWindSpeed()) return false;
        if (saved.getHumidity() != next.getHumidity()) return false;
        if (!Objects.equals(saved.getPrecipitationType(), next.getPrecipitationType())) return false;
        return Objects.equals(saved.getSky(), next.getSky());
    }
}
