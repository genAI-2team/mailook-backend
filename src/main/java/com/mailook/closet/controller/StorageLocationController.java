package com.mailook.closet.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/storage-locations")
@Slf4j
@RestController
public class StorageLocationController {

    @GetMapping
    public void getStorageLocations() {
        // TODO: 보관 위치 목록 조회
    }

    @PostMapping
    public void createStorageLocation() {
        // TODO: 보관 위치 등록
    }

    @PutMapping("/{storageLocationId}")
    public void updateStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
        // TODO: 보관 위치 수정
    }

    @DeleteMapping("/{storageLocationId}")
    public void deleteStorageLocation(@PathVariable("storageLocationId") Long storageLocationId) {
        // TODO: 보관 위치 삭제
    }
}
