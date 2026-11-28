package com.mailook.closet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StorageLocationResponse {
    private final long storageLocationId;
    private final String name;
}
