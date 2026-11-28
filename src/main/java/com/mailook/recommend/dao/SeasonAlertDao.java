package com.mailook.recommend.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mailook.recommend.entity.SeasonAlert;
import com.mailook.recommend.enums.AlertStatus;

@Mapper
public interface SeasonAlertDao {

        // 지정 날짜의 사용자 알림을 생성 순서대로 조회 (처리된 알림 포함)
        List<SeasonAlert> findByUserIdAndAlertDate(
                        @Param("userId") Long userId, @Param("alertDate") LocalDate alertDate);

        // 소유자 조건을 포함한 개별건 조회.
        SeasonAlert findByIdAndUserId(
                        @Param("seasonAlertId") Long seasonAlertId,
                        @Param("userId") Long userId);

        // 생성된 ID는 전달한 객체의 seasonAlertId에 설정됨
        int insertSeasonAlert(SeasonAlert seasonAlert);

        // 사용자 소유의 PENDING 알림만 DONE 또는 LATER로 변경. 변경된 행 수 반환
        int updateStatus(
                        @Param("seasonAlertId") Long seasonAlertId,
                        @Param("userId") Long userId,
                        @Param("status") AlertStatus status);
}
