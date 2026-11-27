package com.mailook.closet.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dao.ClothesDao;
import com.mailook.closet.dao.StorageLocationDao;
import com.mailook.closet.dto.ClothesBatchCreateRequest;
import com.mailook.closet.dto.ClothesBatchCreateRequest.Piece;
import com.mailook.closet.entity.Clothes;

import lombok.extern.slf4j.Slf4j;

@Service 

@Slf4j 
public class ClothesService {
    private final ClothesDao clothesDao;
    private final StorageLocationDao storageLocationDao;

    public ClothesService(ClothesDao clothesDao, StorageLocationDao storageLocationDao) {
        this.clothesDao = clothesDao;
        this.storageLocationDao = storageLocationDao;
    }

    public int insert(Clothes clothes) {
       return clothesDao.insert(clothes);
       
    }

    public int update(Clothes clothes){
        return clothesDao.update(clothes);
    }


    // 로그인한 사용자의 같은 옷을 여러 벌 등록한다.
    public void insertBatch(long userId, ClothesBatchCreateRequest request) {
        // 요청에서 공통 옷 정보와 벌별 보관 위치 목록을 꺼낸다.
        String imageUrl = request.getImageUrl();
        List<Piece> pieces = request.getPieces();

        // 모든 벌이 공유하는 이미지 URL은 필수이며 최대 500자까지 저장할 수 있다.
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 URL을 확인해 주세요.");
        }
        // 여러 벌 등록 요청에는 첫 번째 벌을 포함해 최소 두 벌이 필요하다.
        if (pieces == null || pieces.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록할 옷을 두 벌 이상 입력해 주세요.");
        }

        // 각 벌의 보관 위치는 로그인한 사용자에게 속한 위치여야 한다.
        // 벌 항목 자체는 필수이며, 위치 ID가 null이면 '지정 없음'이다.
        for (Piece piece : pieces) {
            if (piece == null || (piece.storageLocationId() != null
                    && storageLocationDao.findById(userId, piece.storageLocationId()) == null)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보관 위치를 확인해 주세요.");
            }
        }

        // 이름과 분류값 등 공통 속성만 요청에서 복사한다.
        // 옷 ID와 묶음 ID는 DB에서 만들고, 보관 위치는 벌마다 적용한다.
        Clothes clothes = new Clothes();
        BeanUtils.copyProperties(request, clothes, "clothesId", "userId", "sameGroupId", "storageLocationId");
        // 옷 소유자는 로그인 정보에서 받은 사용자 ID로 지정한다.
        clothes.setUserId(userId);
        // 모든 벌을 한 번의 INSERT로 저장하고 첫 번째 벌의 ID로 묶는다.
        clothesDao.insertBatch(clothes, pieces);
    }

    //소프트 스킬로 바꿔야 한다.
    public int delete(Long clothesId){
        return clothesDao.delete(clothesId);
    }
}
