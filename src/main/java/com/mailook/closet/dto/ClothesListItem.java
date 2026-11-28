package com.mailook.closet.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 내 옷장 목록에서 카드 한 장을 표시하는 데 필요한 응답 데이터다. */
@Getter
@AllArgsConstructor
public class ClothesListItem {
    private final Long id;           // 옷 상세 화면 이동과 사진 조회에 쓰는 옷 ID
    private final String name;       // 카드에 표시하는 옷 이름
    private final String category;   // 카테고리 표시와 필터에 쓰는 분류 코드
    private final BigDecimal clo;    // 따뜻함 표시의 기준이 되는 단열값. 값이 없으면 null
    private final String location;   // 보관 위치 이름. 지정된 위치가 없으면 null
    private final boolean stored;    // 보관 중이면 카드에 배지를 표시
    private final boolean hasPhoto;  // DB에 사진 경로가 있으면 사진 조회를 요청
}
