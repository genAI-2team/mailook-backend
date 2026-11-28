package com.mailook.closet.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dao.StorageLocationDao;
import com.mailook.closet.dto.StorageLocationResponse;
import com.mailook.closet.entity.StorageLocation;

@Service
public class StorageLocationService {
    private final StorageLocationDao storageLocationDao;

    public StorageLocationService(StorageLocationDao storageLocationDao) {
        this.storageLocationDao = storageLocationDao;
    }

    /** 사용자의 삭제되지 않은 보관 위치를 등록 순서대로 조회한다. */
    public List<StorageLocationResponse> findAll(long userId) {
        return storageLocationDao.findAllByUserId(userId).stream()
                .map(location -> toResponse(location))
                .toList();
    }

    /** 사용자의 보관 위치를 등록하고, 생성된 위치 ID와 이름을 반환한다. */
    public StorageLocationResponse create(long userId, String name) {
        // 이름의 앞뒤 공백을 제거한 뒤 1~30자인지 확인한다.
        String trimmedName = name == null ? "" : name.strip();
        if (trimmedName.isEmpty() || trimmedName.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보관 위치 이름을 1~30자로 입력해 주세요.");
        }

        StorageLocation location = new StorageLocation();
        location.setUserId(userId);
        location.setName(trimmedName);
        // 등록 후 DB가 생성한 위치 ID가 location에 설정된다.
        storageLocationDao.insert(location);
        return toResponse(location);
    }

    /** 조회와 등록 응답에 필요한 위치 ID와 이름만 꺼낸다. */
    private static StorageLocationResponse toResponse(StorageLocation location) {
        return new StorageLocationResponse(location.getStorageLocationId(), location.getName());
    }
}
