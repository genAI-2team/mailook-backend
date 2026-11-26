package com.mailook.weather.dao;

import java.time.LocalDateTime;
import java.util.List;
import com.mailook.weather.entity.WeatherSnapshot;
import com.mailook.weather.enums.WeatherKind;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeatherSnapshotDao {
    // 날씨 정보를 저장하는 snapshot. API에서 가져와 저장.
    // 기준은 위, 경도와 시간이 됩니다.
    // 삭제가 어렵기 때문에(FK로서 의존하는

    // 같은 위경도·시각·종류 중 시각이 가장 늦은 줄을 사용. 값이 같다면 기록하지 않고 재사용하도록 한다.
    WeatherSnapshot selectLatest(
            @Param("nx") int nx,
            @Param("ny") int ny,
            @Param("forecastAt") LocalDateTime forecastAt,
            @Param("kind") WeatherKind kind);

    // 새 스냅샷을 생성. createdAt은 DB에서 자동 채움 처리.
    int insert(WeatherSnapshot snapshot);

    // 스냅샷을 조회한다.
    WeatherSnapshot selectById(Long weatherSnapshotId);

    // List로 조회한다. 목록이 비어 있으면 호출하지 않는다. 추천에 사용.
    List<WeatherSnapshot> selectByIds(@Param("ids") List<Long> ids);
}
