package com.mailook.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.mailook.user.entity.Login;

@Mapper
public interface LoginDAO {
    Login selectByEmail(@Param("email") String email);
    int updateLastLogin(@Param("userId") Long userId);
}
