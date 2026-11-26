package com.mailook.recommend.service;

import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public String recommend() {
        //선택값을 AI 추천 조건에 반영
        return "오늘은 니트와 코트를 추천합니다.";
    }

    
}