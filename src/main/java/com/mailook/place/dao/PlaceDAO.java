// [변경 후 — 2026-11-28] InfoDAO → PlaceDAO; user 패키지에서 place 패키지로 이동.
package com.mailook.place.dao;

import com.mailook.global.enums.DayOfWeek;
import com.mailook.place.entity.Place;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** [신규 — 2026-11-27] MyBatis 인터페이스는 @Slf4j 대상이 아니다. 로그는 호출 클래스에서 기록한다. */
@Mapper
public interface PlaceDAO {
    Long lockActiveUser(@Param("userId") Long userId, @Param("email") String email);
    boolean existsHome(@Param("userId") Long userId);
    int insertPlace(Place info);
    int insertSchedules(@Param("placeId") Long placeId, @Param("days") List<DayOfWeek> days);
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.dao;
// 
// import com.mailook.global.enums.DayOfWeek;
// import com.mailook.user.entity.Info;
// import java.util.List;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
// 
// /** [신규 — 2026-11-27] MyBatis 인터페이스는 @Slf4j 대상이 아니다. 로그는 호출 클래스에서 기록한다. */
// @Mapper
// public interface InfoDAO {
//     Long lockActiveUser(@Param("userId") Long userId, @Param("email") String email);
//     boolean existsHome(@Param("userId") Long userId);
//     int insertPlace(Info info);
//     int insertSchedules(@Param("placeId") Long placeId, @Param("days") List<DayOfWeek> days);
// }
// 
