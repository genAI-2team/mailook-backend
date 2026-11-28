// [변경 후 — 2026-11-28] InfoRequest → PlaceRequest; user 패키지에서 place 패키지로 이동.
package com.mailook.place.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mailook.global.enums.DayOfWeek;
import com.mailook.place.enums.IndoorTemp;
import com.mailook.place.enums.PlaceEnvironment;
import com.mailook.place.enums.PlaceType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/** [신규 — 2026-11-27] places.jsx 입력. userId/placeId는 클라이언트에서 받지 않는다. */
@Data
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlaceRequest {
    private Boolean locationConsent;
    private PlaceInput home;
    private PlaceInput activity;

    @Data
    @Slf4j
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PlaceInput {
        private PlaceType placeType;
        private String name;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalTime departTime;
        private LocalTime arriveTime;
        private LocalTime leaveTime;
        private PlaceEnvironment environment;
        private IndoorTemp indoorTemp;
        private List<ScheduleInput> schedules;
    }

    @Data
    @Slf4j
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ScheduleInput {
        private DayOfWeek dayOfWeek;
    }
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.dto;
// 
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// import com.fasterxml.jackson.databind.annotation.JsonNaming;
// import com.mailook.global.enums.DayOfWeek;
// import com.mailook.place.enums.IndoorTemp;
// import com.mailook.place.enums.PlaceEnvironment;
// import com.mailook.place.enums.PlaceType;
// import java.math.BigDecimal;
// import java.time.LocalTime;
// import java.util.List;
// import lombok.Data;
// import lombok.extern.slf4j.Slf4j;
// 
// /** [신규 — 2026-11-27] info.jsx 입력. userId/placeId는 클라이언트에서 받지 않는다. */
// @Data
// @Slf4j
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
// public class InfoRequest {
//     private Boolean locationConsent;
//     private PlaceInput home;
//     private PlaceInput activity;
// 
//     @Data
//     @Slf4j
//     @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//     public static class PlaceInput {
//         private PlaceType placeType;
//         private String name;
//         private String address;
//         private BigDecimal latitude;
//         private BigDecimal longitude;
//         private LocalTime departTime;
//         private LocalTime arriveTime;
//         private LocalTime leaveTime;
//         private PlaceEnvironment environment;
//         private IndoorTemp indoorTemp;
//         private List<ScheduleInput> schedules;
//     }
// 
//     @Data
//     @Slf4j
//     @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//     public static class ScheduleInput {
//         private DayOfWeek dayOfWeek;
//     }
// }
// 
