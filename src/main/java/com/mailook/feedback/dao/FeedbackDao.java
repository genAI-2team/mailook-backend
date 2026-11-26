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
    // 피드백 CRUD 및 피드백과 관련된 테이블들에 대한 처리입니다.
    // 피드백 테이블의 경우 관련 처리가 늘어난다면 Dao를 분리합니다.
    // 부위·요인·상황의 경우 편리성을 위해 테이블 정규화로 처리하였으나 인덱싱 속도에 따라 반정규화할 수 있습니다.

    // 피드백 생성. createdAt은 자동으로 처리.
    int insertFeedback(Feedback feedback);

    // 사용자가 유독 춥게/덥게 느꼈던 부위
    //     NECK, BODY, HAND, LEG, FOOT
    // feedback_body_parts TABLE을 사용
    int insertBodyParts(@Param("feedbackId") Long feedbackId, @Param("bodyParts") List<BodyPart> bodyParts);

    // 피드백에 영향을 준 날씨 요인. 목록이 비어 있으면 호출하지 않는다.
    // WIND, HUMID, SUN, RAIN_SNOW
    // feedback_weather_factors TABLE을 사용
    int insertWeatherFactors(@Param("feedbackId") Long feedbackId, @Param("weatherFactors") List<WeatherFactor> weatherFactors);

    // 그날 상황. 목록이 비어 있으면 호출하지 않는다.
    // MOSTLY_INDOOR, SHORT_OUTDOOR, LONG_OUTDOOR, ACTIVE, INDOOR_HOT, INDOOR_COLD
    // feedback_day_contexts TABLE을 사용.
    int insertDayContexts(@Param("feedbackId") Long feedbackId, @Param("dayContexts") List<DayContext> dayContexts);

    // 착용 기록에 피드백이 이미 있는지를 확인한다. 피드백은 기록 당 1회까지 가능하며, 중복 혹은 수정이 불가능.
    boolean existsByWearRecordId(Long wearRecordId);

    // 피드백과 부위·요인·상황 목록을 함께 조회한다.
    Feedback selectById(Long feedbackId);

    // 성향 정리를 위한 최근 피드백. 최신순으로 limit건을 처리. 부위·요인·상황 테이블까지 채워 돌려줘야 한다.
    List<Feedback> selectRecentByUser(@Param("userId") Long userId, @Param("limit") int limit);

    // 마지막 성향 정리 시각 이후의 피드백. since가 null이면 그 사용자 피드백의 전체로 판단한다.
    List<Feedback> selectByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
