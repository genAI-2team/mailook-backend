package com.mailook.preference.entity;

import java.time.LocalDateTime;
import com.mailook.preference.enums.ChangedBy;
import lombok.Data;
import lombok.NoArgsConstructor;

// preference_histories 한 줄.
@Data
@NoArgsConstructor
public class PreferenceHistory {
    private Long preferenceHistoryId;
    private Long userId;
    private String field;
    private String beforeValue;
    private String afterValue;
    private ChangedBy changedBy;
    private String reason;
    private LocalDateTime createdAt;
}
