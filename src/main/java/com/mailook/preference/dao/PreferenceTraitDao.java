package com.mailook.preference.dao;

import java.util.List;
import com.mailook.preference.entity.PreferenceTrait;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreferenceTraitDao {
    // 성향 문장과 관련한 처리입니다.

    // 성향 문장 List. includeRejected가 false면 거절하지 않은 것만, 근거 건수 내림차순.
    List<PreferenceTrait> selectByUser(@Param("userId") Long userId, @Param("includeRejected") boolean includeRejected);

    // 사용자 성향 문장 조회
    PreferenceTrait selectById(Long preferenceTraitId);

    // 새 성향 문장 insert
    int insert(PreferenceTrait trait);

    // 문장과 근거를 수정
    int updateContent(PreferenceTrait trait);

    // 해당 성향 분석을 적용하지 않는 수정
    int updateRejected(@Param("preferenceTraitId") Long preferenceTraitId, @Param("userId") Long userId,
                       @Param("rejected") boolean rejected);
}
