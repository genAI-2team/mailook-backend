// [변경 후 — 2026-11-28] InfoResponse → PlaceResponse; user 패키지에서 place 패키지로 이동.
package com.mailook.place.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/** [신규 — 2026-11-27] 등록된 장소 ID와 활동 일정 수만 반환한다. */
@Data
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlaceResponse {
    private final Long homePlaceId;
    private final Long activityPlaceId;
    private final int scheduleCount;
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.dto;
// 
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// import com.fasterxml.jackson.databind.annotation.JsonNaming;
// import lombok.Data;
// import lombok.extern.slf4j.Slf4j;
// 
// /** [신규 — 2026-11-27] 등록된 장소 ID와 활동 일정 수만 반환한다. */
// @Data
// @Slf4j
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
// public class InfoResponse {
//     private final Long homePlaceId;
//     private final Long activityPlaceId;
//     private final int scheduleCount;
// }
// 
