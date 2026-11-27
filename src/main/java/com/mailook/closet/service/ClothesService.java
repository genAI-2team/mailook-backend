package com.mailook.closet.service;

import org.springframework.stereotype.Service;

import com.mailook.closet.dao.ClothesDao;
import com.mailook.closet.entity.Clothes;

import lombok.extern.slf4j.Slf4j;

@Service 

@Slf4j 
public class ClothesService {
    private final ClothesDao clothesDao;

    public ClothesService(ClothesDao clothesDao){
        this.clothesDao = clothesDao;
    }

    public int insert(Clothes clothes) {
         log.info("2222222222222222222222222222222222222222222222");
       return clothesDao.insert(clothes);
       
    }

    public int update(Clothes clothes){
        return clothesDao.update(clothes);
    }

    //소프트 스킬로 바꿔야 한다.
    public int delete(Long clothesId){
        return clothesDao.delete(clothesId);
    }
}
