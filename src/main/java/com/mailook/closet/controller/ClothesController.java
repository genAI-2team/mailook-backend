package com.mailook.closet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailook.closet.dto.ClothesCreateRequest;
import com.mailook.closet.dto.ClothesResponse;
import com.mailook.closet.entity.Clothes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clothes")
public class ClothesController {

    // 옷 목록 조회
    @GetMapping
    public ResponseEntity<Void> getClothes() {
        // TODO: 목록 조회 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 상세 조회
    @GetMapping("/{clothesId}")
    public ResponseEntity<Void> getClothesDetail(@PathVariable("clothesId") Long clothesId) {
        // TODO: 상세 조회 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 등록 전 옷 분류
    @PostMapping("/classify")
    public ResponseEntity<Void> classifyClothes() {
        // TODO: 분류 요청 데이터 정의 및 분류 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 등록
    @PostMapping
    public ClothesResponse createClothes(@RequestBody ClothesCreateRequest clothesCreateRequest) {
        // TODO: 등록 요청 DTO 정의 및 등록 서비스 연결
        Clothes clothes = new Clothes();
        //clothes.set;

        //Clothes dbClothes = 
        ClothesResponse clothesResponse = new ClothesResponse();
        return clothesResponse;
    }

    // 옷 수정
    @PutMapping("/{clothesId}")
    public ResponseEntity<Void> updateClothes(@PathVariable("clothesId") Long clothesId) {
        // TODO: 수정 요청 DTO 정의 및 수정 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 삭제
    @DeleteMapping("/{clothesId}")
    public ResponseEntity<Void> deleteClothes(@PathVariable("clothesId") Long clothesId) {
        // TODO: 삭제 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 복사
    @PostMapping("/{clothesId}/copy")
    public ResponseEntity<Void> copyClothes(@PathVariable("clothesId") Long clothesId) {
        // TODO: 복사 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 재분류
    @PostMapping("/{clothesId}/reclassify")
    public ResponseEntity<Void> reclassifyClothes(@PathVariable("clothesId") Long clothesId) {
        // TODO: 재분류 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 보관 정보 변경
    @PatchMapping("/{clothesId}/storage")
    public ResponseEntity<Void> updateClothesStorage(@PathVariable("clothesId") Long clothesId) {
        // TODO: 보관 정보 요청 DTO 정의 및 변경 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 태그 수정
    @PutMapping("/{clothesId}/tags")
    public ResponseEntity<Void> updateClothesTags(@PathVariable("clothesId") Long clothesId) {
        // TODO: 태그 요청 DTO 정의 및 수정 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

// 반환을 뭘로 반환할 지는 나중에. <<  현재 반환이 전부 NOT_IMPLEMENTED (501 오류)로 반환함
// 아직 미완
