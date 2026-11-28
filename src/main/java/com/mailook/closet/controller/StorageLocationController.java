package com.mailook.closet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dto.StorageLocationCreateRequest;
import com.mailook.closet.dto.StorageLocationResponse;
import com.mailook.closet.service.StorageLocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/storage-locations")
@RequiredArgsConstructor
public class StorageLocationController {
    private final StorageLocationService storageLocationService;

    @GetMapping
    public List<StorageLocationResponse> getStorageLocations(
            @AuthenticationPrincipal(expression = "claims['userId']") Number userId) {
        return storageLocationService.findAll(requireUserId(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StorageLocationResponse createStorageLocation(
            @RequestBody StorageLocationCreateRequest request,
            @AuthenticationPrincipal(expression = "claims['userId']") Number userId) {
        return storageLocationService.create(requireUserId(userId), request.getName());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStorageLocationError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message", exception.getReason()));
    }

    private long requireUserId(Number userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보를 확인할 수 없습니다.");
        }
        return userId.longValue();
    }

    @PutMapping("/{storageLocationId}")
    public ResponseEntity<Void> updateStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
        // TODO: 새 이름을 받는 요청 DTO와 수정 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{storageLocationId}")
    public ResponseEntity<Void> deleteStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
        // TODO: 보관 위치를 논리 삭제하고 연결된 옷의 보관 위치를 null로 바꾸는 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
