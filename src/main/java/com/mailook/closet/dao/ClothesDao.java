package com.mailook.closet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mailook.closet.entity.Clothes;

@Mapper 
public interface ClothesDao {
    // c
    public int insert(Clothes clothes);
    // u
    public int update(Clothes clothes);
    // r
    // 페이징 처리 나중에
    public List<Clothes> selectAll();
    // d
    public int delete(Long clothesId);  // 옷은 soft방식으로 삭제.
}
