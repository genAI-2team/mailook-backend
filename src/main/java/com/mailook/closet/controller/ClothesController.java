package com.mailook.closet.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dto.ClothesCreateRequest;
import com.mailook.closet.dto.ClothesListItem;
import com.mailook.closet.dto.ClothesResponse;
import com.mailook.closet.enums.ClothesCategory;
import com.mailook.closet.enums.StorageStatus;
import com.mailook.closet.service.ClothesService;


@RestController
@RequestMapping("/api/clothes")
public class ClothesController {
    private final ClothesService clothesService;

    public ClothesController(ClothesService clothesService){
        this.clothesService = clothesService;
    }

    // 옷 목록 조회
    @GetMapping
    public List<ClothesListItem> getClothes(
            @RequestParam(name = "category", required = false) ClothesCategory category,
            @RequestParam(name = "storageStatus", required = false) StorageStatus storageStatus,
            @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
        return clothesService.findAll(userId.longValue(), category, storageStatus);
    }

    @GetMapping("/{clothesId}/photo")
    public ResponseEntity<Resource> getClothesPhoto(
            @PathVariable("clothesId") long clothesId, @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
        Resource photo = clothesService.findPhoto(userId.longValue(), clothesId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaTypeFactory.getMediaType(photo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(photo);
    }

    // 옷 상세 조회
    @GetMapping("/{clothesId}")
    public ResponseEntity<ClothesResponse> getClothesDetail(@PathVariable("clothesId") Long clothesId) {
        // TODO: 상세 조회 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 등록 전 옷 분류
    @PostMapping("/classify")
    public ResponseEntity<ClothesResponse> classifyClothes() {
        // TODO: 분류 요청 데이터 정의 및 분류 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 옷 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesResponse> createClothes(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("data") ClothesCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || !(jwt.getClaim("userId") instanceof Number userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
        clothesService.createClothes(userId.longValue(), request, image);
        HttpStatus status = request.getPieces() == null ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(new ClothesResponse("옷 등록 성공", null));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ClothesResponse> handleClothesError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ClothesResponse("fail", exception.getReason()));
    }

    // 옷 수정
    // @PutMapping("/{clothesId}")
    // public ResponseEntity<Void> updateClothes(@PathVariable("clothesId") Long clothesId) {
    //     // TODO: 수정 요청 DTO 정의 및 수정 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }

    // 옷 삭제
    // @DeleteMapping("/{clothesId}")
    // public ResponseEntity<Void> deleteClothes(@PathVariable("clothesId") Long clothesId) {
    //     // TODO: 삭제 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }

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
    // @PatchMapping("/{clothesId}/storage")
    // public ResponseEntity<Void> updateClothesStorage(@PathVariable("clothesId") Long clothesId) {
    //     // TODO: 보관 정보 요청 DTO 정의 및 변경 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }

    // 옷 태그 수정
    // @PutMapping("/{clothesId}/tags")
    // public ResponseEntity<Void> updateClothesTags(@PathVariable("clothesId") Long clothesId) {
    //     // TODO: 태그 요청 DTO 정의 및 수정 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }
}

// 반환을 뭘로 반환할 지는 나중에. <<  현재 반환이 전부 NOT_IMPLEMENTED (501 오류)로 반환함
// 아직 미완
