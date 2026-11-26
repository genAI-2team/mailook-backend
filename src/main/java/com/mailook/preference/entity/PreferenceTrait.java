package com.mailook.preference.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

// preference_traits 한 줄.
@Data
@NoArgsConstructor
public class PreferenceTrait {
    private Long preferenceTraitId;
    private Long userId;
    private String content;
    private int evidenceCount;
    private boolean rejected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
