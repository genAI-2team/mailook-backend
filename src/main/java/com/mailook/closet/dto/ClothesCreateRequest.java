package com.mailook.closet.dto;

import java.math.BigDecimal;
import java.util.List;

import com.mailook.closet.enums.ClothesCategory;
import com.mailook.closet.enums.Material;
import com.mailook.closet.enums.StorageStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 옷 등록 시 multipart 요청의 data 부분으로 받는 옷 정보.
 * 사진 파일은 image 부분으로 따로 받고, 서버가 파일을 저장한 뒤 경로를
 * Clothes 엔티티의 imageUrl에 설정하므로 이 DTO에는 imageUrl 필드가 없다.
 */
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class ClothesCreateRequest {
    private Long clothesId;
    private Long userId;

    private String name;  //옷 이름
    private ClothesCategory category;   // 옷 종류 - 큰 카테고리
    private String subType;  // 세부 카테고리
    private Material material;  // 소재

    // 보온력. CARRY_ITEM은 null
    private BigDecimal clo;

    // 통풍 정도(1~3). CARRY_ITEM은 null
    private Integer breathability;

    // 목을 덮는지 여부. 상의 외에는 false
    private boolean neckCover;

     // null이면 보관 위치 지정 없음
    private Long storageLocationId;

    // 바로 꺼낼 수 있는 상태인가?
    private StorageStatus storageStatus;

    // 사용자가 AI 분류값을 수정했는지 여부
    private boolean userEdited;

    // 같은 옷 묶음 ID. 새 옷은 자기 clothesId, 복사한 옷은 원본의 sameGroupId
    private Long sameGroupId;

    // 여러 벌 등록 시 첫 번째 벌을 포함한 각 벌의 보관 위치
    private List<Piece> pieces;

    /** 여러 벌 등록에서 옷 한 벌의 보관 위치를 담는다. 이름, 사진, 분류는 요청 전체에서 공유한다. */
    @Data
    public static class Piece {
        // null이면 이 벌은 보관 위치를 지정하지 않는다.
        private Long storageLocationId;
    }
}
