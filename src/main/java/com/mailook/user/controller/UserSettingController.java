package com.mailook.user.controller;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.user.dto.UnwellUpdateRequest;
import com.mailook.user.dto.UserSettingCreateRequest;
import com.mailook.user.dto.UserSettingResponse;
import com.mailook.user.dto.UserSettingUpdateRequest;
import com.mailook.user.entity.UserSetting;
import com.mailook.user.service.UserSettingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 기본 설정. 로그인 사용자의 줄만 다룬다.
@RestController
@RequestMapping("/api/users/me")
public class UserSettingController {

    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    // 객체가 없으면 null. 이 경우 내 성향으로 들어가면 입력하도록 처리한다.
    @GetMapping("/settings")
    public UserSettingResponse settings(@AuthenticationPrincipal Jwt jwt) {
        UserSetting saved = userSettingService.getSetting(loginUserId(jwt));
        if (saved == null) {
            return null;
        }
        return toResponse(saved);
    }

    // 기본 설정이 없을 때 생성한다.
    @PostMapping("/settings")
    public UserSettingResponse createSettings(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody UserSettingCreateRequest request) {
        if (request == null
                || request.getWarmthStyle() == null
                || request.getColdSensitivity() == null
                || request.getHeatSensitivity() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, null);
        }
        Long userId = loginUserId(jwt);
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(userId);
        userSetting.setWarmthStyle(request.getWarmthStyle());
        userSetting.setColdSensitivity(request.getColdSensitivity());
        userSetting.setHeatSensitivity(request.getHeatSensitivity());
        userSettingService.insert(userSetting);
        return toResponse(userSettingService.getSetting(userId));
    }

    // 보온 방식과 추위·더위에 대한 사용자의 대답을 수정
    @PutMapping("/settings")
    public UserSettingResponse updateSettings(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody UserSettingUpdateRequest request) {
        if (request == null
                || request.getWarmthStyle() == null
                || request.getColdSensitivity() == null
                || request.getHeatSensitivity() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, null);
        }
        Long userId = loginUserId(jwt);
        UserSetting next = new UserSetting();
        next.setUserId(userId);
        next.setWarmthStyle(request.getWarmthStyle());
        next.setColdSensitivity(request.getColdSensitivity());
        next.setHeatSensitivity(request.getHeatSensitivity());
        userSettingService.update(next);
        return toResponse(userSettingService.getSetting(userId));
    }

    // 몸 상태와 다시 물어본 날을 수정.
    @PatchMapping("/unwell")
    public UserSettingResponse updateUnwell(@AuthenticationPrincipal Jwt jwt,
                                             @RequestBody UnwellUpdateRequest request) {
        if (request == null || request.getUnwell() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, null);
        }
        Long userId = loginUserId(jwt);
        UserSetting next = new UserSetting();
        next.setUserId(userId);
        next.setUnwell(request.getUnwell());
        next.setUnwellAskedOn(request.getUnwellAskedOn());
        userSettingService.updateUnwell(next);
        return toResponse(userSettingService.getSetting(userId));
    }

    private Long loginUserId(Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
        }
        return userId.longValue();
    }

    // Response 객체로 바꾸는 메서드
    private UserSettingResponse toResponse(UserSetting setting) {
        UserSettingResponse response = new UserSettingResponse();
        response.setWarmthStyle(setting.getWarmthStyle());
        response.setColdSensitivity(setting.getColdSensitivity());
        response.setHeatSensitivity(setting.getHeatSensitivity());
        response.setUnwell(setting.isUnwell());
        response.setUnwellAskedOn(setting.getUnwellAskedOn());
        return response;
    }
}
