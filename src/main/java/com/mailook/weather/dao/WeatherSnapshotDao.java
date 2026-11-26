package com.mailook.weather.dao;

import java.time.LocalDateTime;
import java.util.List;
import com.mailook.weather.entity.WeatherSnapshot;
import com.mailook.weather.enums.WeatherKind;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeatherSnapshotDao {

    WeatherSnapshot selectLatest(
            @Param("nx") int nx,
            @Param("ny") int ny,
            @Param("forecastAt") LocalDateTime forecastAt,
            @Param("kind") WeatherKind kind);

    int insert(WeatherSnapshot snapshot);

    WeatherSnapshot selectById(Long weatherSnapshotId);

    List<WeatherSnapshot> selectByIds(@Param("ids") List<Long> ids);
}
