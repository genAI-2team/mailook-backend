package com.mailook.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;
import com.mailook.weather.dao.WeatherSnapshotDao;
import com.mailook.weather.entity.WeatherSnapshot;
import com.mailook.weather.enums.WeatherKind;
import org.junit.jupiter.api.Test;

// 스냅샷은 같은 발표이거나 값이 같으면 새 줄을 만들지 않는다.
class WeatherServiceTest {

    private final LocalDateTime forecastAt = LocalDateTime.of(2026, 1, 15, 8, 0);
    private final LocalDateTime now = LocalDateTime.of(2026, 1, 15, 8, 30);

    @Test
    void baseAt_forecastWaitsTenMinutes() {
        assertEquals(LocalDateTime.of(2026, 1, 15, 5, 0),
                WeatherService.baseAt(WeatherKind.FORECAST, LocalDateTime.of(2026, 1, 15, 8, 9)));
        assertEquals(LocalDateTime.of(2026, 1, 15, 8, 0),
                WeatherService.baseAt(WeatherKind.FORECAST, LocalDateTime.of(2026, 1, 15, 8, 10)));
    }

    @Test
    void baseAt_observedWaitsUntilForty() {
        assertEquals(LocalDateTime.of(2026, 1, 15, 7, 0),
                WeatherService.baseAt(WeatherKind.OBSERVED, LocalDateTime.of(2026, 1, 15, 8, 39)));
        assertEquals(LocalDateTime.of(2026, 1, 15, 8, 0),
                WeatherService.baseAt(WeatherKind.OBSERVED, LocalDateTime.of(2026, 1, 15, 8, 40)));
    }

    // 이미 이번 발표 줄이 있으면 모의를 다시 만들지 않는다
    @Test
    void reusesSnapshotFromTheSameAnnouncement() {
        MemoryDao dao = new MemoryDao();
        WeatherSnapshot stored = stored(WeatherService.baseAt(WeatherKind.FORECAST, now));
        dao.latest = stored;
        WeatherService service = service(dao);

        WeatherSnapshot result = service.getSnapshot(60, 127, forecastAt, WeatherKind.FORECAST, now);

        assertSame(stored, result);
        assertEquals(0, dao.inserts);
    }

    // 발표는 달라도 기온·바람·습도가 같으면 새 줄을 만들지 않는다
    @Test
    void reusesSnapshotWhenWeatherIsUnchanged() {
        MemoryDao dao = new MemoryDao();
        WeatherService service = service(dao);
        WeatherSnapshot first = service.getSnapshot(60, 127, forecastAt, WeatherKind.FORECAST, now);
        dao.latest = first;
        dao.latest.setBaseAt(first.getBaseAt().minusHours(3));

        WeatherSnapshot again = service.getSnapshot(60, 127, forecastAt, WeatherKind.FORECAST,
                now.plusHours(3));

        assertSame(first, again);
        assertEquals(1, dao.inserts);
    }

    private WeatherService service(MemoryDao dao) {
        return new WeatherService(dao, new MockWeatherClient("WINTER"), new FeelsLikeCalculator());
    }

    private WeatherSnapshot stored(LocalDateTime baseAt) {
        WeatherSnapshot snapshot = new WeatherSnapshot();
        snapshot.setWeatherSnapshotId(1L);
        snapshot.setBaseAt(baseAt);
        return snapshot;
    }

    private static class MemoryDao implements WeatherSnapshotDao {
        private WeatherSnapshot latest;
        private int inserts;

        @Override
        public WeatherSnapshot selectLatest(int nx, int ny, LocalDateTime forecastAt, WeatherKind kind) {
            return latest;
        }

        @Override
        public int insert(WeatherSnapshot snapshot) {
            inserts++;
            snapshot.setWeatherSnapshotId(10L);
            latest = snapshot;
            return 1;
        }

        @Override
        public WeatherSnapshot selectById(Long weatherSnapshotId) {
            return latest;
        }

        @Override
        public List<WeatherSnapshot> selectByIds(List<Long> ids) {
            return List.of();
        }
    }
}
