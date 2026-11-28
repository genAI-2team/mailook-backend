// [변경 후 — 2026-11-28] InfoController → PlaceController; user 패키지에서 place 패키지로 이동.
package com.mailook.place.controller;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.place.dto.PlaceRequest;
import com.mailook.place.dto.PlaceResponse;
import com.mailook.place.service.PlaceService;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

/** [신규 — 2026-11-27] 로그인한 회원의 기본 정보 등록 전용 API. */
@RestController
// [변경 전 — 2026-11-28] @RequestMapping("/api/auth")
@RequestMapping("/api/places")
@Slf4j
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    // [변경 전 — 2026-11-28] @PostMapping("/info")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> insert(@AuthenticationPrincipal Jwt jwt,
            @RequestBody PlaceRequest request) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
        }
        PlaceResponse result = placeService.insert(userId.longValue(), jwt.getSubject(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", result));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> unreadable() {
        return failure(400, "INVALID_INPUT_VALUE", "요청한 장소 유형·요일·시간 형식을 확인해 주세요.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> statusFailure(ResponseStatusException error) {
        return failure(error.getStatusCode().value(), "PLACE_REGISTRATION_FAILED", error.getReason());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> businessFailure(BusinessException error) {
        return failure(error.getErrorCode().getHttpStatus().value(), error.getErrorCode().name(), error.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> databaseFailure(DataAccessException error) {
        log.error("기본 장소·일정 등록 실패", error);
        return failure(500, "PLACE_REGISTRATION_FAILED", "기본 정보 등록에 실패했습니다. 다시 시도해 주세요.");
    }

    private ResponseEntity<Map<String, Object>> failure(int status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false,
                "error", Map.of("code", code, "message", message)));
    }
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.controller;
// 
// import com.mailook.global.exception.BusinessException;
// import com.mailook.global.exception.ErrorCode;
// import com.mailook.user.dto.InfoRequest;
// import com.mailook.user.dto.InfoResponse;
// import com.mailook.user.service.InfoService;
// import java.util.Map;
// import org.springframework.dao.DataAccessException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.server.ResponseStatusException;
// import lombok.extern.slf4j.Slf4j;
// 
// /** [신규 — 2026-11-27] 로그인한 회원의 기본 정보 등록 전용 API. */
// @RestController
// @RequestMapping("/api/auth")
// @Slf4j
// public class InfoController {
//     private final InfoService infoService;
// 
//     public InfoController(InfoService infoService) {
//         this.infoService = infoService;
//     }
// 
//     @PostMapping("/info")
//     public ResponseEntity<Map<String, Object>> insert(@AuthenticationPrincipal Jwt jwt,
//             @RequestBody InfoRequest request) {
//         if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
//             throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
//         }
//         InfoResponse result = infoService.insert(userId.longValue(), jwt.getSubject(), request);
//         return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", result));
//     }
// 
//     @ExceptionHandler(HttpMessageNotReadableException.class)
//     public ResponseEntity<Map<String, Object>> unreadable() {
//         return failure(400, "INVALID_INPUT_VALUE", "요청한 장소 유형·요일·시간 형식을 확인해 주세요.");
//     }
// 
//     @ExceptionHandler(ResponseStatusException.class)
//     public ResponseEntity<Map<String, Object>> statusFailure(ResponseStatusException error) {
//         return failure(error.getStatusCode().value(), "INFO_REGISTRATION_FAILED", error.getReason());
//     }
// 
//     @ExceptionHandler(BusinessException.class)
//     public ResponseEntity<Map<String, Object>> businessFailure(BusinessException error) {
//         return failure(error.getErrorCode().getHttpStatus().value(), error.getErrorCode().name(), error.getMessage());
//     }
// 
//     @ExceptionHandler(DataAccessException.class)
//     public ResponseEntity<Map<String, Object>> databaseFailure(DataAccessException error) {
//         log.error("기본 장소·일정 등록 실패", error);
//         return failure(500, "INFO_REGISTRATION_FAILED", "기본 정보 등록에 실패했습니다. 다시 시도해 주세요.");
//     }
// 
//     private ResponseEntity<Map<String, Object>> failure(int status, String code, String message) {
//         return ResponseEntity.status(status).body(Map.of("success", false,
//                 "error", Map.of("code", code, "message", message)));
//     }
// }
// 
