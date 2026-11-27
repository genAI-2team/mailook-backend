package com.mailook.closet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mailook.closet.dto.ClothesBatchCreateRequest.Piece;
import com.mailook.closet.entity.Clothes;

@Mapper 
public interface ClothesDao {
    // c
    public int insert(@RequestBody Clothes clothes);

    // 2벌 이상 등록
    public int insertBatch(@Param("clothes") Clothes clothes, @Param("pieces") List<Piece> pieces);
    // u
    public int update(@RequestBody Clothes clothes);
    // r
    // 페이징 처리 나중에
    public List<Clothes> selectAll();
    // d
    public int delete(@RequestParam Long clothesId);  // 옷은 soft방식으로 삭제.
}
