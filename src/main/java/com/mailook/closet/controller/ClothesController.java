package com.mailook.closet.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/clothes")
@Slf4j
@RestController
public class ClothesController {

    // 목록 조회
    @GetMapping
    public void getClothesList() {
    }

    // 상세 조회
    @GetMapping("/{clothesId}")
    public void getClothes(@PathVariable("clothesId") Long clothesId) {
    }

    // 등록 전 분류, 사진을 올리면 AI 분류 결과만 돌려줌 (저장 안 함)
    @PostMapping("/classify")
    public void classifyClothes() {
    }

    // 등록
    @PostMapping
    public void createClothes() {
    }

    // 수정
    @PutMapping("/{clothesId}")
    public void updateClothes(@PathVariable("clothesId") Long clothesId) {
    }

    // 삭제
    @DeleteMapping("/{clothesId}")
    public void deleteClothes(@PathVariable("clothesId") Long clothesId) {
    }

    // 복사
    @PostMapping("/{clothesId}/copy")
    public void copyClothes(@PathVariable("clothesId") Long clothesId) {
    }

    // 재분류
    @PostMapping("/{clothesId}/reclassify")
    public void reclassifyClothes(@PathVariable("clothesId") Long clothesId) {
    }

    // 보관 위치 변경
    @PatchMapping("/{clothesId}/storage")
    public void updateClothesStorage(@PathVariable("clothesId") Long clothesId) {
    }

    // 태그 교체
    @PutMapping("/{clothesId}/tags")
    public void replaceClothesTags(@PathVariable("clothesId") Long clothesId) {
    }
}
