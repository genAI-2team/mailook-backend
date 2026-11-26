package com.mailook.feedback.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.mailook.feedback.enums.BodyPart;
import com.mailook.feedback.enums.DayContext;
import com.mailook.feedback.enums.PreferenceChoice;
import com.mailook.feedback.enums.Sensation;
import com.mailook.feedback.enums.WeatherFactor;
import com.mailook.global.enums.WeatherMoment;
import lombok.Data;
import lombok.NoArgsConstructor;

// feedbacks 한 줄. 부위·요인·상황 목록은 자식 테이블에서 채운다.
@Data
@NoArgsConstructor
public class Feedback {
    private Long feedbackId;
    private Long userId;
    private Long wearRecordId;
    private PreferenceChoice preference;
    private Sensation sensation;
    private String memo;
    private WeatherMoment moment;
    private Long placeId;
    private Long weatherSnapshotId;
    private LocalDateTime createdAt;
    private List<BodyPart> bodyParts;
    private List<WeatherFactor> weatherFactors;
    private List<DayContext> dayContexts;
}
