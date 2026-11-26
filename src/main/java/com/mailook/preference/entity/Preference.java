package com.mailook.preference.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

// preferences 한 줄. PK는 user_id.
@Data
@NoArgsConstructor
public class Preference {
    private Long userId;
    private int coldOffset;
    private int heatOffset;
    private int windSensitivity;
    private int humidSensitivity;
    private int sunSensitivity;
    private boolean coldOffsetLocked;
    private boolean heatOffsetLocked;
    private boolean windLocked;
    private boolean humidLocked;
    private boolean sunLocked;
    private int feedbackCount;
    private LocalDateTime summarizedAt;
    private LocalDateTime updatedAt;
}
