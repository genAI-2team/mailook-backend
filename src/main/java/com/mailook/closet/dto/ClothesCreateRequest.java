package com.mailook.closet.dto;

import java.math.BigDecimal;

import com.mailook.closet.enums.ClothesCategory;
import com.mailook.closet.enums.Material;
import com.mailook.closet.enums.StorageStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class ClothesCreateRequest {
    private Long clothesId;
    private Long userId;

    private String imageUrl;
   
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
}
