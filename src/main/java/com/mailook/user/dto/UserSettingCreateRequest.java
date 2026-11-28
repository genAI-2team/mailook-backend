package com.mailook.user.dto;

import com.mailook.user.enums.SensitivityLevel;
import com.mailook.user.enums.WarmthStyle;
import lombok.Data;
import lombok.NoArgsConstructor;

// Setting Create Request
@Data
@NoArgsConstructor
public class UserSettingCreateRequest {
    private WarmthStyle warmthStyle;
    private SensitivityLevel coldSensitivity;
    private SensitivityLevel heatSensitivity;
}
