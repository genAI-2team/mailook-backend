package com.mailook.user.dto;

import java.time.LocalDate;
import com.mailook.user.enums.SensitivityLevel;
import com.mailook.user.enums.WarmthStyle;
import lombok.Data;
import lombok.NoArgsConstructor;

// 기본 설정 조회. 보온 방식, 추위·더위에 대한 답변, 몸 상태.
@Data
@NoArgsConstructor
public class UserSettingResponse {
    private WarmthStyle warmthStyle;
    private SensitivityLevel coldSensitivity;
    private SensitivityLevel heatSensitivity;
    private boolean unwell;
    private LocalDate unwellAskedOn;
}
