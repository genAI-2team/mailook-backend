package com.mailook.recommend.controller;

import com.mailook.recommend.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendations")
    public String recommend() { //요청을 받아 선택값을 서비스에 전달
        return recommendationService.recommend();
    }
}