package com.mailook.preference.dao;

import java.util.List;
import com.mailook.preference.entity.PreferenceHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreferenceHistoryDao {
    // 성향 log를 기록하는 Dao입니다. 삭제 처리는 현재 구현하지 않습니다.
    // 삭제처리가 필요하다고 판단되면 그때 추가합니다.

    // 성향 값이 바뀐 이력. created_at은 DB가 채운다.
    int insert(PreferenceHistory history);

    // 내 성향 화면의 변경 기록. 최신순.
    List<PreferenceHistory> selectByUser(@Param("userId") Long userId, @Param("limit") int limit, @Param("offset") int offset);
}
