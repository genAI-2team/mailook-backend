package com.mailook.closet.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mailook.closet.dao.ClothesDao;
import com.mailook.closet.dao.StorageLocationDao;
import com.mailook.closet.dto.ClothesCreateRequest;
import com.mailook.closet.dto.ClothesPieceCreateRequest;
import com.mailook.closet.entity.Clothes;

import lombok.extern.slf4j.Slf4j;

/** 옷 등록, 수정, 삭제를 처리하고 등록 시 사진과 보관 위치를 함께 관리한다. */
@Service 

@Slf4j 
public class ClothesService {
    private final ClothesDao clothesDao;
    private final StorageLocationDao storageLocationDao;

    /** 옷 저장과 보관 위치 확인에 사용하는 DAO를 주입받는다. */
    public ClothesService(ClothesDao clothesDao, StorageLocationDao storageLocationDao) {
        this.clothesDao = clothesDao;
        this.storageLocationDao = storageLocationDao;
    }

    /** 인증된 사용자의 옷 한 벌 또는 여러 벌을 첨부 사진과 함께 등록한다. */
    public void createClothes(long userId, ClothesCreateRequest request, MultipartFile image) {
        // 분류값을 엔티티에 복사하고, 소유자 ID는 인증에서 전달받은 값으로 설정한다.
        Clothes clothes = new Clothes();
        clothes.setUserId(userId);
        clothes.setClothesId(request.getClothesId());
        clothes.setName(request.getName());
        clothes.setCategory(request.getCategory());
        clothes.setSubType(request.getSubType());
        clothes.setMaterial(request.getMaterial());
        clothes.setClo(request.getClo());
        clothes.setBreathability(request.getBreathability());
        clothes.setNeckCover(request.isNeckCover());
        clothes.setStorageLocationId(request.getStorageLocationId());
        clothes.setStorageStatus(request.getStorageStatus());
        clothes.setUserEdited(request.isUserEdited());
        clothes.setSameGroupId(request.getSameGroupId());

        // pieces가 없으면 한 벌을 등록한다. 여러 벌의 보관 위치는 insertBatch에서 검증한다.
        List<ClothesPieceCreateRequest> pieces = request.getPieces();
        if (pieces == null) {
            validateStorageLocation(userId, clothes.getStorageLocationId());
        }

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옷 사진을 첨부해 주세요.");
        }

        // 파일의 첫 바이트를 읽어 실제 이미지 형식을 확인한다.
        String contentType;
        try (var input = new BufferedInputStream(image.getInputStream())) {
            contentType = URLConnection.guessContentTypeFromStream(input);
            // 일부 JPEG는 표준 감지기가 인식하지 못하므로 JPEG 시작 바이트도 확인한다.
            if (contentType == null && input.read() == 0xFF && input.read() == 0xD8 && input.read() == 0xFF) {
                contentType = "image/jpeg";
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옷 사진을 읽을 수 없습니다.", exception);
        }
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case null, default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "JPG, PNG, GIF 사진을 첨부해 주세요.");
        };

        // 사용자별 디렉터리에 파일을 한 번 저장하고, DB에는 상대 경로를 기록한다.
        Path photo = null;
        try {
            Path directory = Path.of(System.getProperty("user.home"), ".mailook", "clothes-photos", Long.toString(userId));
            Files.createDirectories(directory);
            photo = Files.createTempFile(directory, "photo-", extension);
            image.transferTo(photo);
            clothes.setImageUrl(userId + "/" + photo.getFileName());
            if (pieces == null) {
                clothesDao.insert(clothes);
            } else {
                insertBatch(clothes, pieces);
            }
        } catch (IOException | RuntimeException exception) {
            // 파일 복사나 DB 등록에 실패하면 이번 요청에서 만든 사진을 정리한다.
            if (photo != null) {
                try {
                    Files.deleteIfExists(photo);
                } catch (IOException cleanupFailure) {
                    // 원래 실패 원인을 유지하면서 파일 정리 중 발생한 오류도 기록한다.
                    exception.addSuppressed(cleanupFailure);
                }
            }
            if (exception instanceof RuntimeException failure) {
                throw failure;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "옷 사진을 저장하지 못했습니다.", exception);
        }
    }

    /** 옷 정보를 수정하고 DAO가 반환한 변경 행 수를 돌려준다. */
    // public int update(Clothes clothes){
    //     return clothesDao.update(clothes);
    // }


    /** 같은 옷을 벌마다 한 행씩 저장하며, 모든 행이 사진 경로와 그룹 ID를 공유한다. */
    private void insertBatch(Clothes clothes, List<ClothesPieceCreateRequest> pieces) {
        // 여러 벌 등록 요청에는 첫 번째 벌을 포함해 최소 두 벌이 필요하다.
        if (pieces.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록할 옷을 두 벌 이상 입력해 주세요.");
        }

        // 각 벌의 보관 위치는 로그인한 사용자에게 속한 위치여야 한다.
        // 벌 항목 자체는 필수이며, 위치 ID가 null이면 '지정 없음'이다.
        for (ClothesPieceCreateRequest piece : pieces) {
            if (piece == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보관 위치를 확인해 주세요.");
            }
            validateStorageLocation(clothes.getUserId(), piece.getStorageLocationId());
        }

        // 매퍼는 한 번의 INSERT로 저장하고 첫 번째 벌의 ID를 same_group_id로 사용한다.
        clothesDao.insertBatch(clothes, pieces);
    }

    /** 위치가 지정된 경우에만 해당 사용자의 삭제되지 않은 위치인지 확인한다. */
    private void validateStorageLocation(long userId, Long locationId) {
        if (locationId != null && storageLocationDao.findById(userId, locationId) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보관 위치를 확인해 주세요.");
        }
    }

    /** 옷 삭제를 DAO에 위임하고 변경 행 수를 반환한다. */
    // TODO: 옷 삭제에 소프트 삭제를 적용한다.
    // public int delete(Long clothesId){
    //     return clothesDao.delete(clothesId);
    // }
}
