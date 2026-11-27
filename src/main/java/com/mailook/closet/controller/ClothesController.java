package com.mailook.closet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dto.ClothesCreateRequest;
import com.mailook.closet.dto.ClothesResponse;
import com.mailook.closet.entity.Clothes;
import com.mailook.closet.service.ClothesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clothes")
public class ClothesController {
    private final ClothesService clothesService;

    public ClothesController(ClothesService clothesService){
        this.clothesService = clothesService;
    }

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
    public ClothesResponse createClothes(@RequestBody ClothesCreateRequest clothesCreateRequest,
                                         @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
         log.info("========================================");
        Clothes clothes = new Clothes();
        clothes.setClothesId(clothesCreateRequest.getClothesId());
        clothes.setUserId(userId.longValue());

        //파일 등록 이전에 URL 등록부터 처리
        clothes.setImageUrl(clothesCreateRequest.getImageUrl());

        clothes.setName(clothesCreateRequest.getName());
        clothes.setCategory(clothesCreateRequest.getCategory());
        clothes.setSubType(clothesCreateRequest.getSubType());
        clothes.setMaterial(clothesCreateRequest.getMaterial());
        clothes.setClo(clothesCreateRequest.getClo());
        clothes.setBreathability(clothesCreateRequest.getBreathability());
        clothes.setNeckCover(clothesCreateRequest.isNeckCover());
        clothes.setStorageLocationId(clothesCreateRequest.getStorageLocationId());
        clothes.setStorageStatus(clothesCreateRequest.getStorageStatus());

        clothes.setUserEdited(clothesCreateRequest.isUserEdited());
        clothes.setSameGroupId(clothesCreateRequest.getSameGroupId());

        clothesService.insert(clothes);
        log.info("1111111111111111111111111111111111111111");

        ClothesResponse clothesResponse = new ClothesResponse();
         log.info("333333333333333333333333333333333333");
        clothesResponse.setStatus("옷 등록 성공");
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
