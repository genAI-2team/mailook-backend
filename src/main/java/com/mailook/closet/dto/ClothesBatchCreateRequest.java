package com.mailook.closet.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClothesBatchCreateRequest extends ClothesCreateRequest {
    private List<Piece> pieces;

    public record Piece(Long storageLocationId) {}
}
