package com.mailook.closet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/storage-locations")
public class StorageLocationController {

    @GetMapping
    public ResponseEntity<Void> getStorageLocations() {
        // TODO: 사용자의 보관 위치 목록 조회 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping
    public ResponseEntity<Void> createStorageLocation() {
        // TODO: 보관 위치 이름을 받는 요청 DTO와 등록 서비스 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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
