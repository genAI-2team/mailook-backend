package com.mailook.closet.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class StorageLocation {
    // DB에서 자동 생성하는 보관 위치 ID
    private long storageLocationId;

    // 이 보관 위치를 등록한 사용자의 ID
    private long userId;

    // 화면에 표시하는 보관 위치 이름 (예: 안방 옷장)
    private String name;

    // 논리 삭제 시각. null이면 사용 중인 위치다.
    private Date deletedAt;

    // 보관 위치를 처음 등록한 시각
    private Date createdAt;

}
