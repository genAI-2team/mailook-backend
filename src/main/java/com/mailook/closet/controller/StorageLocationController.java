package com.mailook.closet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dto.StorageLocationCreateRequest;
import com.mailook.closet.dto.StorageLocationResponse;
import com.mailook.closet.service.StorageLocationService;

import lombok.RequiredArgsConstructor;

/** 옷장 보관 위치 API의 요청을 받고 처리 결과를 응답한다. */
@RestController
@RequestMapping("/api/storage-locations")
@RequiredArgsConstructor
public class StorageLocationController {
    private final StorageLocationService storageLocationService;

    /** JWT의 userId를 사용해 로그인한 사용자의 활성 보관 위치를 조회한다. */
    @GetMapping
    public List<StorageLocationResponse> getStorageLocations(
            @AuthenticationPrincipal(expression = "claims['userId']") Number userId) {
        return storageLocationService.findAll(requireUserId(userId));
    }

    /** 요청 본문의 이름으로 보관 위치를 등록하고, 위치 ID와 이름을 201 응답으로 반환한다. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StorageLocationResponse createStorageLocation(
            @RequestBody StorageLocationCreateRequest request,
            @AuthenticationPrincipal(expression = "claims['userId']") Number userId) {
        return storageLocationService.create(requireUserId(userId), request.getName());
    }

    /** 예외의 HTTP 상태 코드와 오류 사유를 응답에 담는다. 오류 사유는 message 필드로 전달한다. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStorageLocationError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message", exception.getReason()));
    }

    /** 사용자 ID가 없으면 401 오류를 반환하고, 있으면 서비스에서 사용하는 long으로 변환한다. */
    private long requireUserId(Number userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보를 확인할 수 없습니다.");
        }
        return userId.longValue();
    }

    /** 보관 위치 이름을 변경하는 경로. 현재는 501을 반환한다. */
    // @PutMapping("/{storageLocationId}")
    // public ResponseEntity<Void> updateStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
    //     // TODO: 새 이름을 받는 요청 DTO와 수정 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }

    /** 보관 위치를 삭제하는 경로. 현재는 501을 반환한다. */
    // @DeleteMapping("/{storageLocationId}")
    // public ResponseEntity<Void> deleteStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
    //     // TODO: 보관 위치를 논리 삭제하고 연결된 옷의 보관 위치를 null로 바꾸는 서비스 연결
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }
}
