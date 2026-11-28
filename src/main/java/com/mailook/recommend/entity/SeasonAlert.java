package com.mailook.recommend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mailook.recommend.enums.AlertStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * season_alerts 테이블에 대응하는 환절기 옷 꺼내기 알림.
 * 전날 배치에서 생성하고 홈 화면에서 조회한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class SeasonAlert {

    private Long seasonAlertId;
    private Long userId;

    // 꺼내면 좋을 보관 중인 옷 ID
    private Long clothesId;

    // 환절기 카드에 표시할 안내 문구
    private String message;
    // 225자까지 가능 

    // 카드를 보여줄 날짜
    private LocalDate alertDate;

    // PENDING 대기 / DONE 꺼냈어요 / LATER 나중에
    private AlertStatus status;

    private LocalDateTime createdAt;
}
