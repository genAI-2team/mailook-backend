package com.mailook.user.dao;

import java.util.List;
import java.util.Map;
import com.mailook.user.dto.Pager;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mailook.user.entity.Login;

@Mapper
public interface LoginDAO {
    // Insert: 데이터를 저장하는 메소드(C)
    int insert(Login login);

    // Select: 기본키로 조회하는 메소드(R)
    Login selectById(@Param("userId") Long userId);

    // 행수를 반환하는 메소드
    int selectCount(@Param("where") Map<String, Object> where);

    // Select: 검색 조건과 페이징으로 데이터를 가져오는 메소드(R)
    List<Login> selectPage(@Param("pager") Pager pager, @Param("where") Map<String, Object> where);

    // Update: 주어진 값으로 수정하는 메소드(U)
    int update(Login login);

    // Delete: 기본키로 탈퇴 처리하는 메소드(D)
    int deleteById(Long userId);

    // 로그인 및 회원 기본 설정에 필요한 추가 메소드
    Login selectByEmail(@Param("email") String email);
    int updateLastLogin(@Param("userId") Long userId);
    int updateLastLogout(@Param("userId") Long userId);
    int insertUserSettings(@Param("userId") Long userId);
    int insertPreferences(@Param("userId") Long userId);
}
