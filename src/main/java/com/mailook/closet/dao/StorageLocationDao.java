package com.mailook.closet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mailook.closet.entity.StorageLocation;


// 옷 보관하는 DAO
@Mapper
public interface StorageLocationDao {

    // 사용자의 새 보관 위치를 등록하고 등록된 행 수를 반환한다.
    int insert(StorageLocation location);

    // 삭제되지 않은 사용자의 보관 위치를 등록 순서로 조회한다.
    List<StorageLocation> findAllByUserId(@Param("userId") Long userId);

    // 삭제되지 않은 사용자의 보관 위치를 조회한다. 해당 위치가 없으면 null을 반환한다.
    StorageLocation findById(@Param("userId") Long userId,
            @Param("storageLocationId") Long storageLocationId);

    // 사용자에게 속한 활성 보관 위치의 이름을 수정하고 수정된 행 수를 반환한다.
    int update(@Param("userId") Long userId,
            @Param("storageLocationId") Long storageLocationId,
            @Param("name") String name);

    // 보관 위치의 삭제 시각을 기록하고 변경된 행 수를 반환한다.
    int delete(@Param("userId") Long userId,
            @Param("storageLocationId") Long storageLocationId);


}
