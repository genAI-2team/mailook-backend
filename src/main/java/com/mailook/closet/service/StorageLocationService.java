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

    public List<StorageLocationResponse> findAll(long userId) {
        return storageLocationDao.findAllByUserId(userId).stream()
                .map(StorageLocationService::toResponse)
                .toList();
    }

    public StorageLocationResponse create(long userId, String name) {
        String trimmedName = name == null ? "" : name.strip();
        if (trimmedName.isEmpty() || trimmedName.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보관 위치 이름을 1~30자로 입력해 주세요.");
        }

        StorageLocation location = new StorageLocation();
        location.setUserId(userId);
        location.setName(trimmedName);
        storageLocationDao.insert(location);
        return toResponse(location);
    }

    private static StorageLocationResponse toResponse(StorageLocation location) {
        return new StorageLocationResponse(location.getStorageLocationId(), location.getName());
    }
}
