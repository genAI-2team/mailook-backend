package com.mailook.user.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

// 몸 상태와 몸 상태에 대해 다시 물어본 날.
@Data
@NoArgsConstructor
public class UnwellUpdateRequest {
    private Boolean unwell;
    private LocalDate unwellAskedOn;
}
