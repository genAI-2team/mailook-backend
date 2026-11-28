package com.mailook.closet.dto;

import lombok.Data;

/** 여러 벌을 등록할 때 옷 한 벌의 보관 위치를 담는다. 이름, 사진, 분류는 요청 전체에서 공유한다. */
@Data
public class ClothesPieceCreateRequest {
    // null이면 이 벌은 보관 위치를 지정하지 않는다.
    private Long storageLocationId;
}
