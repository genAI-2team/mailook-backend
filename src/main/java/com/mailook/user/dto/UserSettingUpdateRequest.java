package com.mailook.user.dto;

import com.mailook.user.enums.SensitivityLevel;
import com.mailook.user.enums.WarmthStyle;
import lombok.Data;
import lombok.NoArgsConstructor;

// Setting Update 상태
@Data
@NoArgsConstructor
public class UserSettingUpdateRequest {
    private WarmthStyle warmthStyle;
    private SensitivityLevel coldSensitivity;
    private SensitivityLevel heatSensitivity;
}
