package com.mailook.feedback.dao;

import java.time.LocalDateTime;
import java.util.List;
import com.mailook.feedback.entity.Feedback;
import com.mailook.feedback.enums.BodyPart;
import com.mailook.feedback.enums.DayContext;
import com.mailook.feedback.enums.WeatherFactor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FeedbackDao {

    int insertFeedback(Feedback feedback);

    int insertBodyParts(@Param("feedbackId") Long feedbackId, @Param("bodyParts") List<BodyPart> bodyParts);

    int insertWeatherFactors(@Param("feedbackId") Long feedbackId, @Param("weatherFactors") List<WeatherFactor> weatherFactors);

    int insertDayContexts(@Param("feedbackId") Long feedbackId, @Param("dayContexts") List<DayContext> dayContexts);

    boolean existsByWearRecordId(Long wearRecordId);

    Feedback selectById(Long feedbackId);

    List<Feedback> selectRecentByUser(@Param("userId") Long userId, @Param("limit") int limit);

    List<Feedback> selectByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
