package com.mailook.user.dao;

import com.mailook.user.entity.UserSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingDao {

    // 기본 설정 조회
    UserSetting selectByUserId(Long userId);

    // 회원가입 후 기본 설정 등록. userId는 요청 시 받는다
    int insert(UserSetting userSetting);

    // 보온 방식과 추위·더위 답을 수정한다.
    int update(UserSetting userSetting);

    // 몸 상태와 다시 물어본 날을 수정한다.
    void updateUnwell(UserSetting userSetting);
}
