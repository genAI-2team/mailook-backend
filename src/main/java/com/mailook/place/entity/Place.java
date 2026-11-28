// [변경 후 — 2026-11-28] Info → Place; user 패키지에서 place 패키지로 이동.
package com.mailook.place.entity;

import com.mailook.place.enums.IndoorTemp;
import com.mailook.place.enums.PlaceEnvironment;
import com.mailook.place.enums.PlaceType;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/** [신규 — 2026-11-27] public.places INSERT 모델. placeId는 DB 생성 키를 받는다. */
@Data
@Slf4j
public class Place {
    private Long placeId;
    private Long userId;
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
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.entity;
// 
// import com.mailook.place.enums.IndoorTemp;
// import com.mailook.place.enums.PlaceEnvironment;
// import com.mailook.place.enums.PlaceType;
// import java.math.BigDecimal;
// import java.time.LocalTime;
// import lombok.Data;
// import lombok.extern.slf4j.Slf4j;
// 
// /** [신규 — 2026-11-27] public.places INSERT 모델. placeId는 DB 생성 키를 받는다. */
// @Data
// @Slf4j
// public class Info {
//     private Long placeId;
//     private Long userId;
//     private PlaceType placeType;
//     private String name;
//     private String address;
//     private BigDecimal latitude;
//     private BigDecimal longitude;
//     private LocalTime departTime;
//     private LocalTime arriveTime;
//     private LocalTime leaveTime;
//     private PlaceEnvironment environment;
//     private IndoorTemp indoorTemp;
// }
// 
