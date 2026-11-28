// [변경 후 — 2026-11-28] InfoService → PlaceService; user 패키지에서 place 패키지로 이동.
package com.mailook.place.service;

import com.mailook.global.exception.BusinessException;
import com.mailook.global.exception.ErrorCode;
import com.mailook.global.enums.DayOfWeek;
import com.mailook.place.enums.IndoorTemp;
import com.mailook.place.enums.PlaceEnvironment;
import com.mailook.place.enums.PlaceType;
import com.mailook.place.dao.PlaceDAO;
import com.mailook.place.dto.PlaceRequest;
import com.mailook.place.dto.PlaceResponse;
import com.mailook.place.entity.Place;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

/** [신규 — 2026-11-27] HOME/ACTIVITY → 선택 요일 일정 순서로 최초 등록한다. 수정·삭제는 없다. */
@Service
@Slf4j
public class PlaceService {
    private final PlaceDAO placeDAO;

    public PlaceService(PlaceDAO placeDAO) {
        this.placeDAO = placeDAO;
    }

    // 일정 INSERT를 포함해 어느 한 단계라도 실패하면 두 장소도 함께 롤백한다.
    @Transactional
    public PlaceResponse insert(Long userId, String email, PlaceRequest request) {
        if (userId == null || userId <= 0 || email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
        }
        if (request == null || !Boolean.TRUE.equals(request.getLocationConsent())) {
            invalid("위치 정보 이용에 동의해 주세요.");
        }
        validate(request.getHome(), PlaceType.HOME);
        validate(request.getActivity(), PlaceType.ACTIVITY);

        // 동일 회원의 동시 저장을 직렬화하고 JWT userId와 subject(email)를 DB와 대조한다.
        if (placeDAO.lockActiveUser(userId, email) == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
        }
        if (placeDAO.existsHome(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "기본 장소가 이미 등록되어 있습니다. 장소 설정에서 확인해 주세요.");
        }

        Place home = toEntity(userId, request.getHome());
        Place activity = toEntity(userId, request.getActivity());
        insertPlace(home);
        insertPlace(activity);
        List<DayOfWeek> days = request.getActivity().getSchedules().stream()
                .map(PlaceRequest.ScheduleInput::getDayOfWeek).toList();
        if (placeDAO.insertSchedules(activity.getPlaceId(), days) != days.size()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활동 장소 일정 등록에 실패했습니다.", null);
        }
        return new PlaceResponse(home.getPlaceId(), activity.getPlaceId(), days.size());
    }

    private void insertPlace(Place info) {
        if (placeDAO.insertPlace(info) != 1 || info.getPlaceId() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "장소 등록에 실패했습니다.", null);
        }
    }

    private void validate(PlaceRequest.PlaceInput place, PlaceType expected) {
        if (place == null || place.getPlaceType() != expected) invalid("집과 활동 장소 유형을 확인해 주세요.");
        if (place.getName() == null || place.getName().isBlank() || place.getName().trim().length() > 30) {
            invalid("장소 이름은 1~30자로 입력해 주세요.");
        }
        if (place.getAddress() == null || place.getAddress().isBlank() || place.getAddress().trim().length() > 100) {
            invalid("검색한 동 단위 주소를 확인해 주세요.");
        }
        if (!coordinate(place.getLatitude(), 90) || !coordinate(place.getLongitude(), 180)) {
            invalid("장소의 위도와 경도를 확인해 주세요.");
        }
        if (place.getArriveTime() == null) invalid("도착 또는 귀가 시간을 선택해 주세요.");
        if (expected == PlaceType.HOME) {
            if (place.getDepartTime() == null || place.getLeaveTime() != null
                    || place.getEnvironment() != null || place.getIndoorTemp() != null
                    || (place.getSchedules() != null && !place.getSchedules().isEmpty())) {
                invalid("집의 출발·귀가 시간과 장소 정보를 확인해 주세요.");
            }
        } else {
            if (place.getDepartTime() != null || place.getLeaveTime() == null || place.getEnvironment() == null) {
                invalid("활동 장소의 도착·출발 시간과 환경을 선택해 주세요.");
            }
            IndoorTemp temp = place.getIndoorTemp();
            if (place.getEnvironment() == PlaceEnvironment.OUTDOOR ? temp != null : temp == null) {
                invalid("야외는 실내 온도를 비우고, 실내 또는 혼합 환경은 실내 온도를 선택해 주세요.");
            }
            if (place.getSchedules() == null || place.getSchedules().isEmpty() || place.getSchedules().size() > 7) {
                invalid("활동 장소에 가는 요일을 1개 이상 선택해 주세요.");
            }
            EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
            for (PlaceRequest.ScheduleInput schedule : place.getSchedules()) {
                if (schedule == null || schedule.getDayOfWeek() == null || !days.add(schedule.getDayOfWeek())) {
                    invalid("가는 요일이 누락되거나 중복되었습니다.");
                }
            }
        }
    }

    private boolean coordinate(BigDecimal value, int limit) {
        return value != null && value.abs().compareTo(BigDecimal.valueOf(limit)) <= 0;
    }

    private Place toEntity(Long userId, PlaceRequest.PlaceInput input) {
        Place info = new Place();
        info.setUserId(userId);
        info.setPlaceType(input.getPlaceType());
        info.setName(input.getName().trim());
        info.setAddress(input.getAddress().trim());
        info.setLatitude(input.getLatitude().setScale(2, RoundingMode.HALF_UP));
        info.setLongitude(input.getLongitude().setScale(2, RoundingMode.HALF_UP));
        info.setDepartTime(input.getDepartTime());
        info.setArriveTime(input.getArriveTime());
        info.setLeaveTime(input.getLeaveTime());
        info.setEnvironment(input.getEnvironment());
        info.setIndoorTemp(input.getIndoorTemp());
        return info;
    }

    private void invalid(String message) {
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, message, null);
    }
}

// [변경 전 — 2026-11-28] 이동 전 소스 보존(실행되지 않음).
// package com.mailook.user.service;
// 
// import com.mailook.global.exception.BusinessException;
// import com.mailook.global.exception.ErrorCode;
// import com.mailook.global.enums.DayOfWeek;
// import com.mailook.place.enums.IndoorTemp;
// import com.mailook.place.enums.PlaceEnvironment;
// import com.mailook.place.enums.PlaceType;
// import com.mailook.user.dao.InfoDAO;
// import com.mailook.user.dto.InfoRequest;
// import com.mailook.user.dto.InfoResponse;
// import com.mailook.user.entity.Info;
// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.util.EnumSet;
// import java.util.List;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.server.ResponseStatusException;
// import lombok.extern.slf4j.Slf4j;
// 
// /** [신규 — 2026-11-27] HOME/ACTIVITY → 선택 요일 일정 순서로 최초 등록한다. 수정·삭제는 없다. */
// @Service
// @Slf4j
// public class InfoService {
//     private final InfoDAO infoDAO;
// 
//     public InfoService(InfoDAO infoDAO) {
//         this.infoDAO = infoDAO;
//     }
// 
//     // 일정 INSERT를 포함해 어느 한 단계라도 실패하면 두 장소도 함께 롤백한다.
//     @Transactional
//     public InfoResponse insert(Long userId, String email, InfoRequest request) {
//         if (userId == null || userId <= 0 || email == null || email.isBlank()) {
//             throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
//         }
//         if (request == null || !Boolean.TRUE.equals(request.getLocationConsent())) {
//             invalid("위치 정보 이용에 동의해 주세요.");
//         }
//         validate(request.getHome(), PlaceType.HOME);
//         validate(request.getActivity(), PlaceType.ACTIVITY);
// 
//         // 동일 회원의 동시 저장을 직렬화하고 JWT userId와 subject(email)를 DB와 대조한다.
//         if (infoDAO.lockActiveUser(userId, email) == null) {
//             throw new BusinessException(ErrorCode.UNAUTHORIZED, null);
//         }
//         if (infoDAO.existsHome(userId)) {
//             throw new ResponseStatusException(HttpStatus.CONFLICT,
//                     "기본 장소가 이미 등록되어 있습니다. 장소 설정에서 확인해 주세요.");
//         }
// 
//         Info home = toEntity(userId, request.getHome());
//         Info activity = toEntity(userId, request.getActivity());
//         insertPlace(home);
//         insertPlace(activity);
//         List<DayOfWeek> days = request.getActivity().getSchedules().stream()
//                 .map(InfoRequest.ScheduleInput::getDayOfWeek).toList();
//         if (infoDAO.insertSchedules(activity.getPlaceId(), days) != days.size()) {
//             throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활동 장소 일정 등록에 실패했습니다.", null);
//         }
//         return new InfoResponse(home.getPlaceId(), activity.getPlaceId(), days.size());
//     }
// 
//     private void insertPlace(Info info) {
//         if (infoDAO.insertPlace(info) != 1 || info.getPlaceId() == null) {
//             throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "장소 등록에 실패했습니다.", null);
//         }
//     }
// 
//     private void validate(InfoRequest.PlaceInput place, PlaceType expected) {
//         if (place == null || place.getPlaceType() != expected) invalid("집과 활동 장소 유형을 확인해 주세요.");
//         if (place.getName() == null || place.getName().isBlank() || place.getName().trim().length() > 30) {
//             invalid("장소 이름은 1~30자로 입력해 주세요.");
//         }
//         if (place.getAddress() == null || place.getAddress().isBlank() || place.getAddress().trim().length() > 100) {
//             invalid("검색한 동 단위 주소를 확인해 주세요.");
//         }
//         if (!coordinate(place.getLatitude(), 90) || !coordinate(place.getLongitude(), 180)) {
//             invalid("장소의 위도와 경도를 확인해 주세요.");
//         }
//         if (place.getArriveTime() == null) invalid("도착 또는 귀가 시간을 선택해 주세요.");
//         if (expected == PlaceType.HOME) {
//             if (place.getDepartTime() == null || place.getLeaveTime() != null
//                     || place.getEnvironment() != null || place.getIndoorTemp() != null
//                     || (place.getSchedules() != null && !place.getSchedules().isEmpty())) {
//                 invalid("집의 출발·귀가 시간과 장소 정보를 확인해 주세요.");
//             }
//         } else {
//             if (place.getDepartTime() != null || place.getLeaveTime() == null || place.getEnvironment() == null) {
//                 invalid("활동 장소의 도착·출발 시간과 환경을 선택해 주세요.");
//             }
//             IndoorTemp temp = place.getIndoorTemp();
//             if (place.getEnvironment() == PlaceEnvironment.OUTDOOR ? temp != null : temp == null) {
//                 invalid("야외는 실내 온도를 비우고, 실내 또는 혼합 환경은 실내 온도를 선택해 주세요.");
//             }
//             if (place.getSchedules() == null || place.getSchedules().isEmpty() || place.getSchedules().size() > 7) {
//                 invalid("활동 장소에 가는 요일을 1개 이상 선택해 주세요.");
//             }
//             EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
//             for (InfoRequest.ScheduleInput schedule : place.getSchedules()) {
//                 if (schedule == null || schedule.getDayOfWeek() == null || !days.add(schedule.getDayOfWeek())) {
//                     invalid("가는 요일이 누락되거나 중복되었습니다.");
//                 }
//             }
//         }
//     }
// 
//     private boolean coordinate(BigDecimal value, int limit) {
//         return value != null && value.abs().compareTo(BigDecimal.valueOf(limit)) <= 0;
//     }
// 
//     private Info toEntity(Long userId, InfoRequest.PlaceInput input) {
//         Info info = new Info();
//         info.setUserId(userId);
//         info.setPlaceType(input.getPlaceType());
//         info.setName(input.getName().trim());
//         info.setAddress(input.getAddress().trim());
//         info.setLatitude(input.getLatitude().setScale(2, RoundingMode.HALF_UP));
//         info.setLongitude(input.getLongitude().setScale(2, RoundingMode.HALF_UP));
//         info.setDepartTime(input.getDepartTime());
//         info.setArriveTime(input.getArriveTime());
//         info.setLeaveTime(input.getLeaveTime());
//         info.setEnvironment(input.getEnvironment());
//         info.setIndoorTemp(input.getIndoorTemp());
//         return info;
//     }
// 
//     private void invalid(String message) {
//         throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, message, null);
//     }
// }
// 
