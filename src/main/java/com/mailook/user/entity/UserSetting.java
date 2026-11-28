package com.mailook.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.mailook.user.enums.SensitivityLevel;
import com.mailook.user.enums.WarmthStyle;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSetting {
    private Long userId;
    private WarmthStyle warmthStyle;
    private SensitivityLevel coldSensitivity;
    private SensitivityLevel heatSensitivity;
    private boolean unwell;
    private LocalDate unwellAskedOn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
