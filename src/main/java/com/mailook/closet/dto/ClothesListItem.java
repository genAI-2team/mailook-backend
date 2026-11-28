package com.mailook.closet.dto;

import java.math.BigDecimal;

import com.mailook.closet.enums.ClothesCategory;
import com.mailook.closet.enums.StorageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 내 옷장 목록에서 카드 한 장을 표시하는 데 필요한 응답 데이터다. */
@Getter
@AllArgsConstructor
public class ClothesListItem {
    private final Long clothesId;    // 카드 대표 옷의 상세 화면과 사진을 조회하는 ID
    private final String name;       // 카드에 표시하는 옷 이름
    private final ClothesCategory category; // 카드의 분류 표시와 카테고리 필터에 사용
    private final BigDecimal clo;    // 따뜻함 표시의 기준이 되는 단열값. 값이 없으면 null
    private final String imageUrl;   // 인증이 필요한 사진 조회 주소. 사진이 없으면 null
    private final StorageStatus storageStatus; // 대표 옷의 보관 상태
    private final Long storageLocationId; // 대표 옷의 보관 위치 ID. 지정하지 않았으면 null
}
