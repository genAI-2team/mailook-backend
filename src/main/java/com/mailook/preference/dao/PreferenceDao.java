package com.mailook.preference.dao;

import com.mailook.preference.entity.Preference;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PreferenceDao {
    // 전반적으로 성향의 경우 수정되지만 삭제되지 않기 때문에(유저에 종속됨) 삭제 로직은 구현하지 않습니다.

    // 사용자 성향 요약
    Preference selectByUserId(Long userId);

    // 회원가입 당시 초기 성향을 생성. 가입시 생성된 userId를 이용
    int insert(Preference preference);

    // 추위·더위 보정과 feedbackCount를 수정
    int updateOffsets(Preference preference);

    // 바람·습도·햇볕 민감도를 수정. updatedAt을 갱신한다
    int updateSensitivities(Preference preference);

    // 사용자가 직접 고칠 때 값과 해당 값에 대한 잠금 처리(Locked)를 설정
    int updateByUser(Preference preference);
}
