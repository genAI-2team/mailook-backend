package com.mailook.recommend.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mailook.recommend.entity.Recommendation;
import com.mailook.recommend.entity.RecommendationItem;
import com.mailook.recommend.entity.RecommendationWeather;

@Mapper
public interface RecommendationDao {
        
        // 사용자 ID와 추천 날짜로 가장 최근 추천 한 건을 조회한다.
        Recommendation findLatestByUserIdAndRecommendDate(
                        @Param("userId") Long userId,
                        @Param("recommendDate") LocalDate recommendDate);

        // 추천 ID와 사용자 ID가 모두 일치하는 추천 한 건을 조회한다.                
        Recommendation findByIdAndUserId(
                        @Param("recommendationId") Long recommendationId,
                        @Param("userId") Long userId);

        // 생성된 ID는 전달한 객체의 recommendationId에 설정됨
        int insertRecommendation(Recommendation recommendation);
        
        int insertRecommendationItem(RecommendationItem item);

        List<RecommendationItem> findItemsByRecommendationIdAndUserId(
                        @Param("recommendationId") Long recommendationId, @Param("userId") Long userId);

        // 날씨 원본이 아닌 추천과 날씨 스냅샷의 연결 정보 저장
        int insertRecommendationWeather(RecommendationWeather weather);

        // 소유자 조건을 확인하며 동선 순서(seq)대로 조회
        List<RecommendationWeather> findWeathersByRecommendationIdAndUserId(
                        @Param("recommendationId") Long recommendationId, @Param("userId") Long userId);
}
