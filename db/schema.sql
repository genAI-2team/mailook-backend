-- mAILOOK schema (PostgreSQL)
-- ERD(mailook_erd_draft.dbml)에서 생성. ERD가 바뀌면 이 파일도 같이 바꿉니다.
-- ENUM 컬럼은 VARCHAR + CHECK (MyBatis 기본 EnumTypeHandler가 enum 이름을 그대로 저장)
-- 데이터 접근: DAO 인터페이스(@Mapper) + XML. 테이블 위 한 줄은 용도(담당), 컬럼 옆은 값의 뜻입니다. 아래 COMMENT ON 은 DB 에도 같은 설명을 남깁니다.
-- id는 BIGSERIAL: clothes 는 <selectKey order="BEFORE">SELECT nextval('clothes_clothes_id_seq')</selectKey> 로 id를 먼저 받음

-- 테이블 전부 삭제 (다시 만들 때 먼저 실행). 데이터도 모두 지워지니 운영 DB에서는 실행하지 않습니다.
DROP TABLE IF EXISTS uniform_days CASCADE;
DROP TABLE IF EXISTS uniform_items CASCADE;
DROP TABLE IF EXISTS uniforms CASCADE;
DROP TABLE IF EXISTS preference_histories CASCADE;
DROP TABLE IF EXISTS preference_traits CASCADE;
DROP TABLE IF EXISTS preferences CASCADE;
DROP TABLE IF EXISTS feedback_weather_factors CASCADE;
DROP TABLE IF EXISTS feedback_day_contexts CASCADE;
DROP TABLE IF EXISTS feedback_body_parts CASCADE;
DROP TABLE IF EXISTS feedbacks CASCADE;
DROP TABLE IF EXISTS weather_snapshots CASCADE;
DROP TABLE IF EXISTS season_alerts CASCADE;
DROP TABLE IF EXISTS recommendation_items CASCADE;
DROP TABLE IF EXISTS recommendation_weathers CASCADE;
DROP TABLE IF EXISTS recommendations CASCADE;
DROP TABLE IF EXISTS wear_record_items CASCADE;
DROP TABLE IF EXISTS wear_records CASCADE;
DROP TABLE IF EXISTS clothes_occasion_tags CASCADE;
DROP TABLE IF EXISTS clothes CASCADE;
DROP TABLE IF EXISTS storage_locations CASCADE;
DROP TABLE IF EXISTS place_schedules CASCADE;
DROP TABLE IF EXISTS places CASCADE;
DROP TABLE IF EXISTS user_settings CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 회원 (정동화). 이메일 로그인, 탈퇴는 상태 변경 + 30일 뒤 배치 삭제
CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,  -- PK
                       email VARCHAR(100) NOT NULL UNIQUE,  -- 로그인 아이디 (UNIQUE)
                       password VARCHAR(255) NOT NULL,  -- BCrypt 해시
                       nickname VARCHAR(100) NOT NULL,  -- 닉네임 (가입 1단계)
                       status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DORMANT', 'WITHDRAWN')),  -- ACTIVE 활동 / DORMANT 휴면(값만, 기능은 나중) / WITHDRAWN 탈퇴
                       withdrawn_at TIMESTAMP,  -- 탈퇴 시각. 30일 동안 같은 이메일 재가입 불가
                       location_agreed_at TIMESTAMP NOT NULL,  -- 위치 정보 이용 동의 시각 (필수)
                       last_login_at TIMESTAMP,  -- 마지막 로그인
                       last_logout_at TIMESTAMP,  -- 마지막 로그아웃
                       body_photo_url VARCHAR(500),  -- P2 내 모습으로 전신 사진 경로. 지우면 null
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- 기본 설정 (정동화). users 와 1:1, 회원가입 트랜잭션에서 같이 insert
CREATE TABLE user_settings (
                               user_id BIGINT PRIMARY KEY,  -- PK 겸 FK (users)
                               warmth_style VARCHAR(30) NOT NULL DEFAULT 'ANY' CHECK (warmth_style IN ('INNER_LAYER', 'OUTER_LAYER', 'ANY')),  -- 선호하는 보온 방식: INNER_LAYER 껴입는 편 / OUTER_LAYER 겉옷을 두껍게 / ANY 상관없음
                               cold_sensitivity VARCHAR(30) NOT NULL DEFAULT 'NORMAL' CHECK (cold_sensitivity IN ('LESS', 'NORMAL', 'MORE')),  -- 추위: LESS 덜 탐 / NORMAL 비슷 / MORE 더 탐. 고치면 resetOffset 으로 cold_offset 초기화
                               heat_sensitivity VARCHAR(30) NOT NULL DEFAULT 'NORMAL' CHECK (heat_sensitivity IN ('LESS', 'NORMAL', 'MORE')),  -- 더위: LESS / NORMAL / MORE. 고치면 resetOffset 으로 heat_offset 초기화
                               unwell BOOLEAN NOT NULL DEFAULT FALSE,  -- "오늘은 몸이 안 좋아요" 켠 상태. 끌 때까지 유지
                               unwell_asked_on DATE,  -- 켠 상태에서 다시 물어본 날. 오늘과 다르면 아침에 한 번 더 물음
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- 집·활동 장소 (정동화). 집(HOME)은 나가는/돌아오는 시간, 활동 장소(ACTIVITY)는 도착/떠나는 시간
CREATE TABLE places (
                        place_id BIGSERIAL PRIMARY KEY,  -- PK
                        user_id BIGINT NOT NULL,  -- FK users
                        place_type VARCHAR(30) NOT NULL CHECK (place_type IN ('HOME', 'ACTIVITY')),  -- HOME 집 / ACTIVITY 활동 장소
                        name VARCHAR(30) NOT NULL,  -- 집, 직장, 학교 등 (최대 30자)
                        address VARCHAR(100) NOT NULL,  -- 동 단위까지만 (예: 서울 강남구 역삼동)
                        latitude NUMERIC(5,2) NOT NULL,  -- 소수점 둘째 자리 반올림 (약 1km), 기상청 격자 변환용
                        longitude NUMERIC(5,2) NOT NULL,  -- latitude 와 같은 규칙
                        depart_time TIME,  -- 집: 나가는 시간 (DEPART 시각). 집은 필수
                        arrive_time TIME,  -- 집: 돌아오는 시간 (표시용, 계산 X) / 활동 장소: 도착 시간
                        leave_time TIME,  -- 활동 장소: 떠나는 시간 (MOVE·RETURN 시각)
                        environment VARCHAR(30) CHECK (environment IN ('INDOOR', 'OUTDOOR', 'MIXED')),  -- 활동 장소: INDOOR 실내 / OUTDOOR 야외 / MIXED 둘 다. 집은 null
                        indoor_temp VARCHAR(30) CHECK (indoor_temp IN ('HOT', 'NORMAL', 'COLD')),  -- 실내 온도: HOT 26° / NORMAL 22° / COLD 18°. 야외면 null
                        deleted_at TIMESTAMP,  -- soft delete. 지난 추천·피드백 날씨가 참조
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- 활동 장소에 가는 요일 (정동화). 장소 1 : 요일 N
CREATE TABLE place_schedules (
                                 place_id BIGINT NOT NULL,  -- FK places
                                 day_of_week VARCHAR(30) NOT NULL CHECK (day_of_week IN ('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN')),  -- MON ~ SUN
                                 deleted_at TIMESTAMP,  -- soft delete
                                 PRIMARY KEY (place_id, day_of_week)
);

-- 옷 보관 위치 (강민주). 이름 UNIQUE 없음
CREATE TABLE storage_locations (
                                   storage_location_id BIGSERIAL PRIMARY KEY,  -- PK
                                   user_id BIGINT NOT NULL,  -- FK users
                                   name VARCHAR(30) NOT NULL,  -- 안방 옷장, 창고 리빙박스 등
                                   deleted_at TIMESTAMP,  -- soft delete. 지우면 그 위치 옷의 storage_location_id 를 null 로
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);

-- 옷 (강민주). 한 벌에 한 줄, 같은 옷 여러 벌은 same_group_id 로 묶음. id 는 selectKey 로 먼저 받음
CREATE TABLE clothes (
                         clothes_id BIGSERIAL PRIMARY KEY,  -- PK. selectKey(BEFORE)로 nextval 먼저 받음
                         user_id BIGINT NOT NULL,  -- FK users
                         storage_location_id BIGINT,  -- FK storage_locations. null 이면 "지정 없음"
                         name VARCHAR(50) NOT NULL,  -- 옷 이름. AI가 채우고 사용자가 고칠 수 있음
                         category VARCHAR(30) NOT NULL CHECK (category IN ('OUTER', 'TOP', 'BOTTOM', 'SHOES', 'ACCESSORY', 'CARRY_ITEM')),  -- OUTER 아우터 / TOP 상의 / BOTTOM 하의 / SHOES 신발 / ACCESSORY 소품 / CARRY_ITEM 우산 등 챙길 것
                         sub_type VARCHAR(30),  -- 코트, 패딩, 니트, 우산 등 세부 종류
                         material VARCHAR(30) CHECK (material IN ('WOOL', 'COTTON', 'POLYESTER', 'DOWN', 'LINEN', 'DENIM', 'ETC')),  -- WOOL 울 / COTTON 면 / POLYESTER / DOWN 다운 / LINEN 린넨 / DENIM 데님 / ETC 기타
                         clo NUMERIC(3,2),  -- 보온력 (ISO 9920). 화면 단계 = round(clo / 0.08), 변환은 화면. CARRY_ITEM 만 null
                         neck_cover BOOLEAN NOT NULL DEFAULT FALSE,  -- 목 부분: false 드러남 / true 덮여짐. 상의만 의미
                         breathability SMALLINT,  -- 통풍 1~3. CARRY_ITEM 만 null
                         storage_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE' CHECK (storage_status IN ('AVAILABLE', 'STORED')),  -- AVAILABLE 꺼내 둠 / STORED 보관 중 (계절 지나 넣어 둠)
                         image_url VARCHAR(500) NOT NULL,  -- 올린 사진 저장소 경로
                         user_edited BOOLEAN NOT NULL DEFAULT FALSE,  -- AI 분류값을 고치면 true. true 면 AI가 다시 분류하지 않음
                         same_group_id BIGINT NOT NULL,  -- 같은 옷 묶음. 혼자면 자기 clothes_id
                         deleted_at TIMESTAMP,  -- soft delete. 지난 기록에는 삭제된 옷으로 남김
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- 옷의 상황 태그 (강민주). 없으면 일상복. LLM 맥락으로만 쓰고 코드로 거르지 않음
CREATE TABLE clothes_occasion_tags (
                                       clothes_id BIGINT NOT NULL,  -- FK clothes
                                       tag VARCHAR(30) NOT NULL CHECK (tag IN ('OUTING', 'WORK', 'SPORTS', 'FORMAL')),  -- OUTING 외출복 / WORK 출근용 / SPORTS 운동복 / FORMAL 격식 있는 자리
                                       PRIMARY KEY (clothes_id, tag)
);

-- 착용 기록 (강민주). 하루 1개, 피드백 전에는 덮어쓰고 피드백 저장되면 잠김
CREATE TABLE wear_records (
                              wear_record_id BIGSERIAL PRIMARY KEY,  -- PK
                              user_id BIGINT NOT NULL,  -- FK users
                              recommendation_id BIGINT,  -- FK recommendations. 추천에서 골랐을 때만, 직접 고르면 null
                              worn_date DATE NOT NULL,  -- 입은 날. (user_id, worn_date) UNIQUE = 하루 1개
                              situation VARCHAR(30) NOT NULL CHECK (situation IN ('INDOOR', 'OUTDOOR', 'MOVING', 'FORMAL')),  -- 오늘 상황: INDOOR 실내 위주 / OUTDOOR 야외 위주 / MOVING 이동 위주 / FORMAL 격식
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                              UNIQUE (user_id, worn_date)
);

-- 그날 실제로 입은 옷 (강민주). 추천의 '바꾸기'로 바꾼 조합도 여기에 저장
CREATE TABLE wear_record_items (
                                   wear_record_id BIGINT NOT NULL,  -- FK wear_records
                                   clothes_id BIGINT NOT NULL,  -- FK clothes
                                   PRIMARY KEY (wear_record_id, clothes_id)
);

-- 추천 (최수인). 버전으로 쌓음: 다른 조합 보기·재추천은 새 줄, 이전 추천을 고치거나 지우지 않음
CREATE TABLE recommendations (
                                 recommendation_id BIGSERIAL PRIMARY KEY,  -- PK
                                 user_id BIGINT NOT NULL,  -- FK users
                                 recommend_date DATE NOT NULL,  -- 추천 날짜. 같은 날 여러 버전 가능
                                 situation VARCHAR(30) NOT NULL CHECK (situation IN ('INDOOR', 'OUTDOOR', 'MOVING', 'FORMAL')),  -- 홈에서 고른 오늘 상황 (wear_records.situation 과 같은 값)
                                 reason TEXT NOT NULL,  -- LLM 추천 이유 (규칙 기반이면 안내 문구)
                                 unwell BOOLEAN NOT NULL DEFAULT FALSE,  -- 추천 때 user_settings.unwell 복사. true 면 기준 온도 -4°, 그날 피드백 학습 제외
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);
CREATE INDEX idx_recommendations_user_id_recommend_date ON recommendations (user_id, recommend_date);

-- 추천에 쓰인 시점별 날씨 (최수인, 시점 목록은 전이레 OutdoorMomentService). FORECAST 스냅샷만 가리킴
CREATE TABLE recommendation_weathers (
                                         recommendation_id BIGINT NOT NULL,  -- FK recommendations
                                         seq SMALLINT NOT NULL,  -- 그날 동선 순서 (1부터)
                                         moment VARCHAR(30) NOT NULL CHECK (moment IN ('DEPART', 'MOVE', 'STAY_OUTDOOR', 'RETURN', 'DAYTIME')),  -- DEPART 집 출발 / MOVE 이동 / STAY_OUTDOOR 야외 체류 / RETURN 귀가 / DAYTIME 일정 없는 날
                                         place_id BIGINT NOT NULL,  -- FK places. 날씨를 받은 장소
                                         weather_snapshot_id BIGINT NOT NULL,  -- FK weather_snapshots (FORECAST)
                                         required_clo NUMERIC(3,2) NOT NULL,  -- 이 시점에 필요한 조합 clo = (31 - 기준 온도) / (0.155 x 활동량)
                                         is_deciding BOOLEAN NOT NULL DEFAULT FALSE,  -- 기준 시점. 하루 1개
                                         PRIMARY KEY (recommendation_id, seq)
);

-- 추천 조합의 옷 (최수인). WEAR 입는 옷 / BRING 챙길 것. 보온 계산에는 WEAR 만
CREATE TABLE recommendation_items (
                                      recommendation_id BIGINT NOT NULL,  -- FK recommendations
                                      clothes_id BIGINT NOT NULL,  -- FK clothes
                                      role VARCHAR(30) NOT NULL DEFAULT 'WEAR' CHECK (role IN ('WEAR', 'BRING')),  -- WEAR 입는 옷 / BRING 챙겨 가면 좋은 것 (CARRY_ITEM 은 항상 BRING)
                                      reason VARCHAR(255),  -- BRING 일 때 챙기면 좋은 이유 (LLM)
                                      PRIMARY KEY (recommendation_id, clothes_id)
);

-- 환절기 카드 (최수인, P1). 전날 배치가 만들고 홈은 읽기만
CREATE TABLE season_alerts (
                               season_alert_id BIGSERIAL PRIMARY KEY,  -- PK
                               user_id BIGINT NOT NULL,  -- FK users
                               clothes_id BIGINT NOT NULL,  -- FK clothes. 꺼내면 좋을 보관 중인 옷
                               message VARCHAR(255) NOT NULL,  -- 카드 문구
                               alert_date DATE NOT NULL,  -- 카드를 보여줄 날
                               status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'DONE', 'LATER')),  -- PENDING 대기 / DONE 꺼냈어요(옷 AVAILABLE 로) / LATER 나중에
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);

-- 날씨 스냅샷 (전이레). 격자·시각 단위로 공유. 덮어쓰지도 지우지도 않고, 같은 (nx, ny, forecast_at, kind) 최신 값과 같으면 재사용
CREATE TABLE weather_snapshots (
                                   weather_snapshot_id BIGSERIAL PRIMARY KEY,  -- PK
                                   nx INTEGER NOT NULL,  -- 기상청 격자 X
                                   ny INTEGER NOT NULL,  -- 기상청 격자 Y
                                   forecast_at TIMESTAMP NOT NULL,  -- 날씨 대상 시각
                                   kind VARCHAR(30) NOT NULL CHECK (kind IN ('FORECAST', 'OBSERVED')),  -- FORECAST 예보(추천) / OBSERVED 관측(피드백)
                                   base_at TIMESTAMP NOT NULL,  -- 기상청 발표 시각 (관측은 관측 시각)
                                   temperature NUMERIC(4,1) NOT NULL,  -- 기온 (°C)
                                   feels_like NUMERIC(4,1) NOT NULL,  -- 체감온도 (°C). 코드로 계산: 10~4월 기온+풍속, 5~9월 기온+습도
                                   wind_speed NUMERIC(4,1) NOT NULL,  -- 풍속 (m/s)
                                   humidity INTEGER NOT NULL,  -- 습도 (%)
                                   precipitation_type VARCHAR(20),  -- 강수 형태 (기상청 PTY 값)
                                   sky VARCHAR(20),  -- 하늘 상태 (기상청 SKY 값)
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                                   UNIQUE (nx, ny, forecast_at, kind, base_at)
);

-- 저녁 체감 피드백 (전이레). 착용 기록 1개에 피드백 1개
CREATE TABLE feedbacks (
                           feedback_id BIGSERIAL PRIMARY KEY,  -- PK
                           user_id BIGINT NOT NULL,  -- FK users
                           wear_record_id BIGINT NOT NULL UNIQUE,  -- FK wear_records (UNIQUE = 착용 기록당 1개). 저장되면 착용 기록 잠김
                           preference VARCHAR(30) NOT NULL CHECK (preference IN ('WARMER', 'KEEP', 'LIGHTER')),  -- 필수: WARMER 더 따뜻하게(+1) / KEEP 이대로(0) / LIGHTER 더 가볍게(-1)
                           sensation VARCHAR(30) CHECK (sensation IN ('COLD', 'CHILLY', 'OK', 'HOT')),  -- 선택: COLD 추웠어요 / CHILLY 쌀쌀함 / OK 괜찮 / HOT 더웠어요
                           memo VARCHAR(200),  -- 선택, 최대 200자. LLM 성향 정리에만 참고, 프롬프트에서 데이터로만
                           moment VARCHAR(30) NOT NULL CHECK (moment IN ('DEPART', 'MOVE', 'STAY_OUTDOOR', 'RETURN', 'DAYTIME')),  -- 기준 시점의 종류 (그날 고른 추천의 is_deciding 시점)
                           place_id BIGINT NOT NULL,  -- FK places. 기준 시점의 장소
                           weather_snapshot_id BIGINT NOT NULL,  -- FK weather_snapshots. 기준 시점의 관측 날씨(OBSERVED, 못 받으면 예보로 대신). 보정 계산에 이 체감을 씀
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);

-- 피드백한 날 실제 시점별 날씨 (전이레). 고른 추천의 시점 목록을 복사하고 날씨만 관측(OBSERVED)으로

-- 추웠던/더웠던 부위 (전이레). 선택·여러 개
CREATE TABLE feedback_body_parts (
                                     feedback_id BIGINT NOT NULL,  -- FK feedbacks
                                     body_part VARCHAR(30) NOT NULL CHECK (body_part IN ('NECK', 'BODY', 'HAND', 'LEG', 'FOOT')),  -- NECK 목 / BODY 몸통 / HAND 손 / LEG 다리 / FOOT 발
                                     PRIMARY KEY (feedback_id, body_part)
);

-- 그날 상황 (전이레). 선택·여러 개, 숫자 계산에는 안 쓰고 LLM 성향 문장에만 참고
CREATE TABLE feedback_day_contexts (
                                       feedback_id BIGINT NOT NULL,  -- FK feedbacks
                                       day_context VARCHAR(30) NOT NULL CHECK (day_context IN ('MOSTLY_INDOOR', 'SHORT_OUTDOOR', 'LONG_OUTDOOR', 'ACTIVE', 'INDOOR_HOT', 'INDOOR_COLD')),  -- MOSTLY_INDOOR 거의 실내 / SHORT_OUTDOOR 바깥 잠깐 / LONG_OUTDOOR 바깥 오래 / ACTIVE 많이 움직임 / INDOOR_HOT 실내 더움 / INDOOR_COLD 실내 추움
                                       PRIMARY KEY (feedback_id, day_context)
);

-- 신경 쓰인 날씨 요인 (전이레). 선택·여러 개
CREATE TABLE feedback_weather_factors (
                                          feedback_id BIGINT NOT NULL,  -- FK feedbacks
                                          weather_factor VARCHAR(30) NOT NULL CHECK (weather_factor IN ('WIND', 'HUMID', 'SUN', 'RAIN_SNOW')),  -- WIND 바람 / HUMID 습함 / SUN 햇볕 / RAIN_SNOW 비·눈
                                          PRIMARY KEY (feedback_id, weather_factor)
);

-- 성향 보정값 (전이레). users 와 1:1, 회원가입 때 추위·더위 답으로 초기값
CREATE TABLE preferences (
                             user_id BIGINT PRIMARY KEY,  -- PK 겸 FK (users)
                             cold_offset SMALLINT NOT NULL DEFAULT 0,  -- 추위 보정 -5 ~ +5 (°C), +면 따뜻하게. 초기값 덜 탐 -4 / 비슷 0 / 더 탐 +4
                             heat_offset SMALLINT NOT NULL DEFAULT 0,  -- 더위 보정 -5 ~ +5 (°C), -면 가볍게. 초기값 덜 탐 +4 / 비슷 0 / 더 탐 -4
                             wind_sensitivity SMALLINT NOT NULL DEFAULT 2,  -- 바람 민감도 1 낮음 / 2 보통 / 3 높음
                             humid_sensitivity SMALLINT NOT NULL DEFAULT 2,  -- 습도 민감도 1~3
                             sun_sensitivity SMALLINT NOT NULL DEFAULT 2,  -- 햇볕 민감도 1~3
                             cold_offset_locked BOOLEAN NOT NULL DEFAULT FALSE,  -- 사용자가 직접 고치면 true, AI가 덮어쓰지 않음
                             heat_offset_locked BOOLEAN NOT NULL DEFAULT FALSE,  -- cold_offset_locked 와 같음
                             wind_locked BOOLEAN NOT NULL DEFAULT FALSE,  -- 같은 규칙
                             humid_locked BOOLEAN NOT NULL DEFAULT FALSE,  -- 같은 규칙
                             sun_locked BOOLEAN NOT NULL DEFAULT FALSE,  -- 같은 규칙
                             feedback_count INTEGER NOT NULL DEFAULT 0,  -- 요약에 쓰인 피드백 수
                             summarized_at TIMESTAMP,  -- 성향 문장을 마지막으로 정리한 시각
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- AI가 정리한 유저 성향 문장 (전이레)
CREATE TABLE preference_traits (
                                   preference_trait_id BIGSERIAL PRIMARY KEY,  -- PK
                                   user_id BIGINT NOT NULL,  -- FK users
                                   content VARCHAR(255) NOT NULL,  -- AI가 정리한 성향 문장 (예: 바람 부는 날 목이 추운 편)
                                   evidence_count INTEGER NOT NULL DEFAULT 0,  -- 이 문장을 뒷받침하는 피드백 수
                                   rejected BOOLEAN NOT NULL DEFAULT FALSE,  -- "나랑 안 맞아요" 누르면 true, 추천에 반영 안 함
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 만든 시각 (자동)
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 고친 시각 (자동)
);

-- 성향 값이 바뀐 이력 (전이레). AI 갱신·사용자 수정·답 변경(resetOffset) 모두 기록
CREATE TABLE preference_histories (
                                      preference_history_id BIGSERIAL PRIMARY KEY,  -- PK
                                      user_id BIGINT NOT NULL,  -- FK users
                                      field VARCHAR(30) NOT NULL,  -- 바뀐 값 이름: cold_offset, heat_offset, wind_sensitivity 등
                                      before_value VARCHAR(30),  -- 바뀌기 전 값 (처음이면 null)
                                      after_value VARCHAR(30) NOT NULL,  -- 바뀐 뒤 값
                                      changed_by VARCHAR(30) NOT NULL CHECK (changed_by IN ('AI', 'USER')),  -- AI 피드백 학습 / USER 직접 수정·답 변경
                                      reason VARCHAR(255),  -- AI 변경 근거
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);

-- 고정 복장 (P2). 교복·작업복처럼 요일마다 정해진 옷
CREATE TABLE uniforms (
                          uniform_id BIGSERIAL PRIMARY KEY,  -- PK
                          user_id BIGINT NOT NULL,  -- FK users
                          name VARCHAR(30) NOT NULL,  -- 고정 복장 이름 (예: 교복)
                          worn_while_commuting BOOLEAN NOT NULL DEFAULT TRUE,  -- true 입고 오감 / false 도착해서 갈아입음
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 만든 시각 (자동)
);

-- 고정 복장에 들어가는 옷 (P2)
CREATE TABLE uniform_items (
                               uniform_id BIGINT NOT NULL,  -- FK uniforms
                               clothes_id BIGINT NOT NULL,  -- FK clothes
                               PRIMARY KEY (uniform_id, clothes_id)
);

-- 고정 복장을 입는 요일 (P2)
CREATE TABLE uniform_days (
                              uniform_id BIGINT NOT NULL,  -- FK uniforms
                              day_of_week VARCHAR(30) NOT NULL CHECK (day_of_week IN ('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN')),  -- MON ~ SUN
                              PRIMARY KEY (uniform_id, day_of_week)
);

-- 외래 키 (기본 ON DELETE RESTRICT)
ALTER TABLE user_settings ADD CONSTRAINT fk_user_settings_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE places ADD CONSTRAINT fk_places_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE place_schedules ADD CONSTRAINT fk_place_schedules_place_id FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE RESTRICT;
ALTER TABLE storage_locations ADD CONSTRAINT fk_storage_locations_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE clothes ADD CONSTRAINT fk_clothes_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE clothes ADD CONSTRAINT fk_clothes_storage_location_id FOREIGN KEY (storage_location_id) REFERENCES storage_locations (storage_location_id) ON DELETE RESTRICT;
ALTER TABLE clothes ADD CONSTRAINT fk_clothes_same_group_id FOREIGN KEY (same_group_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE clothes_occasion_tags ADD CONSTRAINT fk_clothes_occasion_tags_clothes_id FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE wear_records ADD CONSTRAINT fk_wear_records_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE wear_records ADD CONSTRAINT fk_wear_records_recommendation_id FOREIGN KEY (recommendation_id) REFERENCES recommendations (recommendation_id) ON DELETE RESTRICT;
ALTER TABLE wear_record_items ADD CONSTRAINT fk_wear_record_items_wear_record_id FOREIGN KEY (wear_record_id) REFERENCES wear_records (wear_record_id) ON DELETE RESTRICT;
ALTER TABLE wear_record_items ADD CONSTRAINT fk_wear_record_items_clothes_id FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE recommendations ADD CONSTRAINT fk_recommendations_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE recommendation_weathers ADD CONSTRAINT fk_recommendation_weathers_recommendation_id FOREIGN KEY (recommendation_id) REFERENCES recommendations (recommendation_id) ON DELETE RESTRICT;
ALTER TABLE recommendation_weathers ADD CONSTRAINT fk_recommendation_weathers_place_id FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE RESTRICT;
ALTER TABLE recommendation_weathers ADD CONSTRAINT fk_recommendation_weathers_weather_snapshot_id FOREIGN KEY (weather_snapshot_id) REFERENCES weather_snapshots (weather_snapshot_id) ON DELETE RESTRICT;
ALTER TABLE recommendation_items ADD CONSTRAINT fk_recommendation_items_recommendation_id FOREIGN KEY (recommendation_id) REFERENCES recommendations (recommendation_id) ON DELETE RESTRICT;
ALTER TABLE recommendation_items ADD CONSTRAINT fk_recommendation_items_clothes_id FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE season_alerts ADD CONSTRAINT fk_season_alerts_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE season_alerts ADD CONSTRAINT fk_season_alerts_clothes_id FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE feedbacks ADD CONSTRAINT fk_feedbacks_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE feedbacks ADD CONSTRAINT fk_feedbacks_place_id FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE RESTRICT;
ALTER TABLE feedbacks ADD CONSTRAINT fk_feedbacks_weather_snapshot_id FOREIGN KEY (weather_snapshot_id) REFERENCES weather_snapshots (weather_snapshot_id) ON DELETE RESTRICT;
ALTER TABLE feedbacks ADD CONSTRAINT fk_feedbacks_wear_record_id FOREIGN KEY (wear_record_id) REFERENCES wear_records (wear_record_id) ON DELETE RESTRICT;
ALTER TABLE feedback_body_parts ADD CONSTRAINT fk_feedback_body_parts_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;
ALTER TABLE feedback_day_contexts ADD CONSTRAINT fk_feedback_day_contexts_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;
ALTER TABLE feedback_weather_factors ADD CONSTRAINT fk_feedback_weather_factors_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;
ALTER TABLE preferences ADD CONSTRAINT fk_preferences_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE preference_traits ADD CONSTRAINT fk_preference_traits_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE preference_histories ADD CONSTRAINT fk_preference_histories_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE uniforms ADD CONSTRAINT fk_uniforms_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;
ALTER TABLE uniform_items ADD CONSTRAINT fk_uniform_items_uniform_id FOREIGN KEY (uniform_id) REFERENCES uniforms (uniform_id) ON DELETE RESTRICT;
ALTER TABLE uniform_items ADD CONSTRAINT fk_uniform_items_clothes_id FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id) ON DELETE RESTRICT;
ALTER TABLE uniform_days ADD CONSTRAINT fk_uniform_days_uniform_id FOREIGN KEY (uniform_id) REFERENCES uniforms (uniform_id) ON DELETE RESTRICT;

-- 컬럼 설명 (ERD note)
COMMENT ON COLUMN users.user_id IS '숫자형 PK. 로그인용 아이디는 두지 않음';
COMMENT ON COLUMN users.email IS '로그인 아이디로 사용';
COMMENT ON COLUMN users.password IS 'BCrypt 해시 저장 (평문 길이 제한은 가입 검증에서)';
COMMENT ON COLUMN users.nickname IS '닉네임 (가입 1단계에서 받음)';
COMMENT ON COLUMN users.status IS 'ACTIVE 활동중 / DORMANT 휴면 / WITHDRAWN 탈퇴';
COMMENT ON COLUMN users.withdrawn_at IS '탈퇴일. 30일 뒤 배치로 회원과 연결 데이터 완전 삭제 (규칙 논의 2번). 그 30일 동안 같은 이메일로 재가입 불가 (10번)';
COMMENT ON COLUMN users.location_agreed_at IS '위치 정보 이용 동의 시각. 가입 때 체크해야 가입 가능 (규칙 논의 9번)';
COMMENT ON COLUMN users.last_login_at IS '마지막 로그인. 휴면 판단 기준 (휴면 기능은 나중에)';
COMMENT ON COLUMN users.last_logout_at IS '마지막 로그아웃';
COMMENT ON COLUMN users.body_photo_url IS 'P2 내 모습으로(REQ-341) 전신 사진 저장소 경로. 한 번 올리면 다음부터 재사용. 사진 지우기 → null + 저장소 파일 삭제';
COMMENT ON COLUMN user_settings.unwell IS 'P1 "오늘은 몸이 안 좋아요" 켠 상태 (REQ-421). 끌 때까지 유지. 추천을 만들 때 recommendations.unwell 로 복사';
COMMENT ON COLUMN user_settings.unwell_asked_on IS '켠 상태에서 마지막으로 다시 물어본 날. 오늘과 다르면 아침에 홈에서 계속 켤지 한 번 더 물어봄';
COMMENT ON COLUMN user_settings.user_id IS 'PK 겸 FK (1:1). 회원가입 트랜잭션에서 users insert 직후 같은 user_id 로 insert';
COMMENT ON COLUMN user_settings.warmth_style IS '보온 챙기는 방식';
COMMENT ON COLUMN user_settings.cold_sensitivity IS '가입 질문: 남들보다 추위를 타는 편인가요? → preferences.cold_offset 초기값: 덜 탐 -4 / 비슷 0 / 더 탐 +4 (규칙 논의 11번). 성향 생성은 A. 가입 후 이 답을 고치면 cold_offset 을 새 답의 초기값으로 다시 맞추고 preference_histories(changed_by = USER)에 기록';
COMMENT ON COLUMN user_settings.heat_sensitivity IS '가입 질문: 남들보다 더위를 타는 편인가요? → preferences.heat_offset 초기값: 덜 탐 +4 / 비슷 0 / 더 탐 -4. 가입 후 고치면 heat_offset 을 새 초기값으로 다시 맞춤 (cold 와 같은 규칙)';
COMMENT ON COLUMN places.name IS '집 / 직장 / 학교 등 사용자가 정한 이름';
COMMENT ON COLUMN places.address IS '동 단위까지만 저장 (예: 서울 강남구 역삼동). 카카오 검색 결과에서 잘라 저장. LLM 에 장소 맥락으로 넘김';
COMMENT ON COLUMN places.latitude IS '소수점 둘째 자리로 반올림해 저장 (약 1km). 기상청 격자(약 5km) 변환용이라 정확한 위치는 저장하지 않음';
COMMENT ON COLUMN places.longitude IS 'latitude 와 같은 규칙';
COMMENT ON COLUMN places.depart_time IS '집: 나가는 시간';
COMMENT ON COLUMN places.arrive_time IS '집: 돌아오는 시간 (동선 표시용, 귀가 날씨 시각은 마지막 활동 장소 leave_time) / 활동 장소: 도착 시간';
COMMENT ON COLUMN places.leave_time IS '활동 장소: 떠나는 시간';
COMMENT ON COLUMN places.environment IS '활동 장소: 주로 머무는 곳 (실내/야외/둘 다)';
COMMENT ON COLUMN places.indoor_temp IS '활동 장소 실내 온도. 야외면 null';
COMMENT ON COLUMN places.deleted_at IS '삭제 시각 (soft delete). 지난 추천·피드백의 시점별 날씨가 참조';
COMMENT ON COLUMN place_schedules.deleted_at IS 'soft delete (규칙 논의 12번)';
COMMENT ON COLUMN storage_locations.name IS '안방 옷장, 창고 리빙박스 등';
COMMENT ON COLUMN storage_locations.deleted_at IS 'soft delete (규칙 논의 12번). 지우면 이 위치에 있던 옷의 storage_location_id 를 비움 (= 지정 없음)';
COMMENT ON COLUMN clothes.storage_location_id IS '비어 있으면 "지정 없음" (문고리에 걸어 두는 등 정해진 곳 없음, 규칙 논의 12번 추가 의견). 옷이나 보관 위치를 지우면 null 로 비움';
COMMENT ON COLUMN clothes.sub_type IS '코트, 패딩, 니트, 우산 등 세부 종류';
COMMENT ON COLUMN clothes.clo IS '보온력 (ISO 9920 단위, 옷마다 약 0.03~0.8). AI가 종류별 기준표에서 고르고 소재·두께로 조정. 화면의 따뜻한 정도 10단계 = round(clo / 0.08)이고 0 이하는 1 (변환은 화면에서), 사용자가 10칸에서 고치면 화면이 clo = 단계 x 0.08 로 바꿔 보냄. API 는 clo 만 주고받음. CARRY_ITEM 만 null (목도리·장갑 같은 ACCESSORY 는 값 있음)';
COMMENT ON COLUMN clothes.neck_cover IS '목 부분: false 드러남 / true 덮여짐. TOP(상의)만 의미, 나머지 종류는 항상 false';
COMMENT ON COLUMN clothes.breathability IS '통풍 1~3 (소재 보고 AI가 채움). CARRY_ITEM 만 null';
COMMENT ON COLUMN clothes.image_url IS '사용자가 올린 사진의 저장소 경로 (쇼핑몰 사진도 저장해서 올림)';
COMMENT ON COLUMN clothes.user_edited IS 'AI 분류값을 한 칸이라도 고치면 true. true 면 AI가 자동으로 다시 분류하지 않음. 사용자가 "다시 분류"를 누를 때만 AI가 채우고 false 로';
COMMENT ON COLUMN clothes.same_group_id IS '같은 옷 묶음. 혼자인 옷도 자기 clothes_id (self-id). "여러 벌이에요"로 늘린 벌은 원본의 same_group_id. 묶음은 사용자 버튼으로만 생김 (AI 자동 묶기 없음)';
COMMENT ON COLUMN clothes.deleted_at IS '삭제 시각. 지난 기록에는 삭제된 옷으로 남김';
COMMENT ON COLUMN wear_records.recommendation_id IS '추천에서 고른 경우';
COMMENT ON COLUMN recommendations.reason IS 'LLM 추천 이유';
COMMENT ON COLUMN recommendations.unwell IS 'P1 "오늘은 몸이 안 좋아요". true 면 기준 온도를 4° 낮춰 계산(한 단계쯤 따뜻하게), 이날 피드백은 성향 학습에서 제외. 병명·증상은 저장하지 않음';
COMMENT ON COLUMN recommendation_weathers.seq IS '그날 동선 순서 (1부터)';
COMMENT ON COLUMN recommendation_weathers.place_id IS '날씨를 받은 장소. 출발·귀가·일정 없는 날은 집, 이동은 도착 장소, 야외 체류는 그 장소';
COMMENT ON COLUMN recommendation_weathers.required_clo IS '이 시점에 필요한 조합 보온력 = (31 - (체감 - 보정 x 적용 비율)) / (0.155 x 활동량). 바깥 활동량 115, 실내 60 (W/m², ASHRAE 55)';
COMMENT ON COLUMN recommendation_weathers.is_deciding IS '옷을 정한 시점 (required_clo 가 가장 높은 시점). 하루에 1개';
COMMENT ON COLUMN recommendation_items.reason IS 'BRING 일 때 챙기면 좋은 이유 (LLM 작성). 예: 직장 실내가 추웠다는 피드백이 3번 있었어요';
COMMENT ON COLUMN weather_snapshots.nx IS '기상청 격자 X';
COMMENT ON COLUMN weather_snapshots.ny IS '기상청 격자 Y';
COMMENT ON COLUMN weather_snapshots.forecast_at IS '날씨 대상 시각';
COMMENT ON COLUMN weather_snapshots.kind IS '예보 / 관측';
COMMENT ON COLUMN weather_snapshots.base_at IS '기상청 발표 시각 (관측은 관측 시각과 같음)';
COMMENT ON COLUMN weather_snapshots.feels_like IS '코드로 계산. 10~4월 기온+풍속, 5~9월 기온+습도(기상청 공식)';
COMMENT ON COLUMN feedbacks.preference IS '필수: 더 따뜻하게 / 이대로 / 더 가볍게';
COMMENT ON COLUMN feedbacks.sensation IS '선택';
COMMENT ON COLUMN feedbacks.memo IS '선택. 더 남기고 싶은 말. 숫자 계산에는 쓰지 않고 LLM 성향 정리에만 참고 (사용자 입력이라 프롬프트에서 데이터로만 다룸)';
COMMENT ON COLUMN preferences.user_id IS 'PK 겸 FK (1:1). 회원가입 트랜잭션에서 users insert 직후 같은 user_id 로 insert';
COMMENT ON COLUMN preferences.cold_offset IS '추위 보정 -5 ~ +5 (°C). 체감 20° 미만인 시점에 적용. +면 따뜻하게. 단위는 °C: 기준 온도 = 체감 - 보정 x 적용 비율로 넣어 required_clo 를 계산';
COMMENT ON COLUMN preferences.heat_offset IS '더위 보정 -5 ~ +5 (°C). 체감 20° 이상인 시점에 적용. -면 가볍게 (부호 규칙은 cold_offset 과 같음)';
COMMENT ON COLUMN preferences.wind_sensitivity IS '1 낮음 / 2 보통 / 3 높음';
COMMENT ON COLUMN preferences.cold_offset_locked IS '사용자가 직접 고치면 true, AI가 바로 덮어쓰지 않음';
COMMENT ON COLUMN preferences.feedback_count IS '요약에 쓰인 피드백 수';
COMMENT ON COLUMN preference_traits.content IS 'AI가 정리한 유저 성향 문장';
COMMENT ON COLUMN preference_traits.rejected IS '나랑 안 맞아요 → 추천에 반영 안 함';
COMMENT ON COLUMN preference_histories.field IS 'cold_offset, heat_offset, wind_sensitivity 등';
COMMENT ON COLUMN preference_histories.reason IS 'AI 변경 근거';
COMMENT ON COLUMN uniforms.worn_while_commuting IS 'true 입고 오감 / false 도착해서 갈아입음';
