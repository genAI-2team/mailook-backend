package com.mailook.closet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mailook.closet.dto.ClothesPieceCreateRequest;
import com.mailook.closet.entity.Clothes;

@Mapper 
public interface ClothesDao {
    // 옷 추가
    public int insert(@RequestBody Clothes clothes);

    // 옷 2벌 이상 등록
    public int insertBatch(@Param("clothes") Clothes clothes, @Param("pieces") List<ClothesPieceCreateRequest> pieces);
    // 옷 수정
    public int update(@RequestBody Clothes clothes);

    // 페이징 처리 나중에, 옷 모두 조회
    public List<Clothes> selectAll();

    // soft 형식으로 삭제 처리
    public int delete(@RequestParam Long clothesId);  
}
