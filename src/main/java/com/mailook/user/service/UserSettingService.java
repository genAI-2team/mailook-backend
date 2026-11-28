package com.mailook.user.service;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.preference.service.PreferenceService;
import com.mailook.user.dao.UserSettingDao;
import com.mailook.user.entity.UserSetting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 기본 설정. 보온 방식과 추위·더위 답, 몸 상태를 저장한다.
// 사용자에게 귀속되어 있으므로 삭제는 불필요.
@Service
public class UserSettingService {

    private final UserSettingDao userSettingDao;
    private final PreferenceService preferenceService;

    public UserSettingService(UserSettingDao userSettingDao, PreferenceService preferenceService) {
        this.userSettingDao = userSettingDao;
        this.preferenceService = preferenceService;
    }

    // 기본 설정을 불러온다. 홈의 몸 상태도 해당 Service를 사용.
    public UserSetting getSetting(Long userId) {
        return userSettingDao.selectByUserId(userId);
    }

    // 기본 설정을 추가.
    public void insert(UserSetting userSetting) {
        userSettingDao.insert(userSetting);
    }

    // 보온 방식과 추위·더위에 대한 사용자 답변을 고친다. 답변이 바뀌면 보정을 초기값으로 되돌린다.
    @Transactional
    public void update(UserSetting next) {
        UserSetting saved = userSettingDao.selectByUserId(next.getUserId());
        if (saved == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, null);
        }
        userSettingDao.update(next);
        // 성향 줄이 생기기 전에는 보정 초기화를 호출하지 않는다.
        // if (saved.getColdSensitivity() != next.getColdSensitivity()) {
        //     preferenceService.resetOffset(next.getUserId(), "COLD", next.getColdSensitivity());
        // }
        // if (saved.getHeatSensitivity() != next.getHeatSensitivity()) {
        //     preferenceService.resetOffset(next.getUserId(), "HEAT", next.getHeatSensitivity());
        // }
    }

    // 몸 상태 및 다시 물어본 날만 고친다.
    public void updateUnwell(UserSetting userSetting) {
        userSettingDao.updateUnwell(userSetting);
    }
}
