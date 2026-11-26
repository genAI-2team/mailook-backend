package com.mailook.closet.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.mailook.closet.enums.ClothesCategory;
import com.mailook.closet.enums.Material;
import com.mailook.closet.enums.StorageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class Clothes {

    private Long clothesId;
    private Long userId;

    // null이면 보관 위치 지정 없음
    private Long storageLocationId;

    private String name;
    private ClothesCategory category;
    private String subType;
    private Material material;

    // 보온력. CARRY_ITEM은 null
    private BigDecimal clo;

    // 목을 덮는지 여부. 상의 외에는 false
    private boolean neckCover;

    // 통풍 정도(1~3). CARRY_ITEM은 null
    private Integer breathability;

    private StorageStatus storageStatus;
    private String imageUrl;

    // 사용자가 AI 분류값을 수정했는지 여부
    private boolean userEdited;

    // 같은 옷 묶음 ID. 새 옷은 자기 clothesId, 복사한 옷은 원본의 sameGroupId
    private Long sameGroupId;

    // 논리 삭제 시각. deletedAt에서 null이면 삭제되지 않은 옷
    private Date deletedAt;
    private Date createdAt;
    private Date updatedAt;
}
