package com.mailook.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.mailook.user.entity.Signup;

@Mapper
public interface SignupDAO {
    // 등록 전 이메일 중복 확인용 조회. 수정·삭제 기능은 제공하지 않는다.
    boolean existsByEmail(@Param("email") String email);
    int insert(Signup signup);
}
