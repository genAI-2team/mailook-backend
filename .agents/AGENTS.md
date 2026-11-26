# AGENTS.md — mAILOOK 백엔드

이 파일은 mAILOOK **백엔드**에서 작업하는 코딩 에이전트(Codex 등)가 먼저 읽는 안내서입니다. 프론트 규칙은 프론트 쪽 AGENTS.md에 따로 있습니다.
설계의 기준 문서는 Notion(요구사항 정의서, ERD, API 명세 초안, 계층구조 초안)이고, 이 파일은 그 요약입니다. 둘이 다르면 Notion을 따르고, 이 파일을 고쳐 주세요.

## 0. 브랜치 규칙 (작업 전에 반드시 확인)

### 작업 시작 전 확인 순서

1. **현재 브랜치를 먼저 확인합니다** (`git branch --show-current`).
2. **작업 브랜치(`타입/작업-내용`, 예: `feat/closet-crud`)에 있으면** 브랜치를 새로 만들지 않고 그 브랜치에서 바로 작업합니다.
3. **`main` 또는 `develop`에 있으면** 지시받은 작업에 맞는 작업 브랜치를 새로 만든 뒤 작업합니다.
  - 이때 지시가 불분명해서 무엇을 만들거나 고칠지, 어떤 타입(feat/fix/...)인지 정할 수 없으면 **경고 메시지만 남기고 아무 작업도 하지 않습니다.** 브랜치도 만들지 않고, 파일도 고치지 않습니다.
  - 경고 예: `⚠️ 지시가 불분명해 작업하지 않았습니다. 무엇을 바꿔야 하는지(대상 기능·화면·API)와 작업 종류(기능 추가/버그 수정 등)를 알려 주세요.`
  - 작업 브랜치는 항상 **최신 develop에서** 만듭니다. main에 있었다면 develop으로 옮긴 뒤 만듭니다.
   ```bash
   git switch develop
   git pull origin develop
   git switch -c 타입/작업-내용
   ```

### 브랜치 구조

| 브랜치 | 역할 | 규칙 |
|---|---|---|
| `main` | 최종 빌드·발표용 (기본 브랜치) | 직접 커밋·push 금지 |
| `develop` | 개발 통합 브랜치 | 직접 커밋·push 금지 |
| `타입/작업-내용` | 개인 작업 브랜치. develop에서 생성 | 작업 후 develop으로 PR |

- 작업 브랜치 이름: `타입/작업-내용` (소문자, 단어는 하이픈). 예: `feat/closet-crud`, `fix/weather-cache-expire`, `design/home-layout`
- **main과 develop에는 절대 직접 커밋하지 않습니다.** 합치는 것은 PR로만 합니다.

### 타입

| 타입 | 언제 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 (기능 변화 없음) |
| `design` | UI/UX 및 CSS 스타일링 변경 |
| `chore` | 패키지 매니저, 빌드 설정 등 코드 외 변경 |

### 커밋 메시지

- 형식: `타입: 작업 내용` (예: `feat: 옷 등록 시 AI 분류 결과 저장`)
- 제목과 본문은 한국어로 씁니다.
- 한 커밋에는 한 가지 일만 담습니다.

### PR

- 작업 브랜치 → develop. develop → main은 최종 빌드 때만.
- 제목: `[타입] 작업 내용` (타입은 대문자. 예: `[FEAT] 옷장 목록 카테고리 필터 구현`)
- 제목과 본문은 한국어로 씁니다.
- PR 하나에는 기능 하나만 담습니다.
- 아래 본문은 예시입니다. 항목을 다 채울 필요는 없고, 쓸 내용이 없는 항목은 지웁니다. 빈 칸을 맞추려고 없는 확인이나 이슈를 적지 않습니다.
- 본문 예시:
  ```markdown
  ## 무엇을 했나요
  - 한두 줄로 이 PR의 목적

  ## 변경 사항
  -

  ## 확인한 것
  - [ ] 로컬에서 실행 확인
  - [ ] API 응답 확인 (Postman 등)
  - [ ] 화면 확인 (UI 변경 시 스크린샷 첨부)

  ## 리뷰 때 봐주세요
  - 특히 확인이 필요한 부분이나 고민한 지점

  ## 관련 이슈
  - closes #
  ```

### 환경 변수 · 설정 파일

- API 키, DB 비밀번호 같은 값은 코드에 쓰지 않습니다. `.env` 등 실제 값이 든 파일은 커밋하지 않습니다.
- `build.gradle`이나 `application.properties`를 바꿨다면, 무엇을 왜 바꿨는지 작업 결과에 꼭 적어서 사용자가 팀에 공유할 수 있게 합니다.

## 실행 명령

- Gradle: `./gradlew build`, `./gradlew bootRun`
- DB: Docker의 PostgreSQL(포트 5433, DB `postgres`). 접속 정보는 환경변수로 받습니다.

## 1. 프로젝트 한 줄 요약

내 옷장 안에서, 오늘 날씨에 춥지도 덥지도 않은 옷차림을 추천하고, 저녁 체감 피드백으로 개인의 추위·더위 성향을 학습하는 서비스입니다.
학습하는 것은 스타일이 아니라 "이 사람이 얼마나 춥고 덥게 느끼는지"입니다.

핵심 흐름: 옷 등록(AI 분류) → 아침 추천 → "이걸로 입을게요"(착용 기록) → 저녁 피드백 → 성향 갱신 → 다음 추천에 반영

## 2. 기술 스택

- Java 21, Spring Boot, Spring AI 1.1(OpenAI), Spring Security, JWT
- 데이터 접근: **MyBatis (DAO 인터페이스 `@Mapper` + XML)**. DAO는 `@Mapper`를 붙인 인터페이스이고, 메서드 이름이 XML의 SQL id와 이어집니다. JPA를 쓰지 않습니다.
- DB: PostgreSQL(Docker, 포트 5433). **Redis는 당장 쓰지 않습니다**(의존성·설정을 넣지 않음). pgvector는 P2에서만 씁니다.
- 외부 API: 카카오 지도(좌표·주소). **날씨는 지금 모의 데이터(랜덤)로 돌립니다.** 기상청 단기·중기예보 연동은 나중에 같은 인터페이스로 바꿉니다.
- 프론트는 Next.js(JavaScript)입니다. 로그인하면 JWT를 응답 본문으로 주고, 프론트가 localStorage에 저장해 요청마다 `Authorization: Bearer {토큰}` 헤더로 보냅니다.

## 3. 담당

| 담당 | 도메인 |
|---|---|
| 전이레 | 공통 기반 설정(`global/config`: AsyncConfig), LLM 설정(`ai/config`: AiConfig), 공용 ENUM(`global/enums`), 날씨(모의)·체감온도, 보온 계산 공통 모듈, 피드백, 성향(+ `ai/preference`), 프론트 틀 |
| 정동화 | 회원, 설정, 장소·동선(카카오 지도, 요일·시간), 로그인 보안(`global/security`: SecurityConfig·JwtProvider·JwtAuthenticationFilter·LoginUser, Authorization 헤더·CORS) |
| 최수인 | 옷장, 보관 위치, 착용 기록, 옷 사진 AI 분류(`ai/classify`) |
| 강민주 | 추천(+ `ai/recommend`), 챙길 것, 환절기, 공통 기반 일부(응답 형식·예외 처리·에러 코드·시간 자동 기록) |

### 담당 밖 파일 수정 규칙

- **작업을 지시한 사람의 담당 도메인이 아닌 파일을 수정해야 하면, 수정하기 전에 먼저 물어봅니다.** 어떤 파일을, 왜, 어떻게 바꾸려는지 적고, 사용자가 허락한 뒤에만 수정합니다. 묻는 동안 해당 파일은 건드리지 않습니다.
- 공통 영역도 같은 규칙입니다. `global/`, `build.gradle`, `application.properties`, **DB 스키마(`schema.sql`)**는 여러 사람이 함께 쓰므로 고치기 전에 물어봅니다. 테이블·컬럼을 바꾸면 모든 사람의 Mapper XML이 영향을 받습니다.
- API 주소나 요청·응답 모양을 바꾸면 프론트도 영향을 받습니다. 바꾸기 전에 물어보고, 바꾼 내용을 작업 결과에 적습니다.
- 지시한 사람이 누구인지(어느 담당인지) 알 수 없으면, 담당 밖 파일을 고쳐야 하는 시점에 먼저 물어봅니다.
- 담당 도메인 안의 파일은 묻지 않고 작업해도 됩니다.

## 4. 백엔드 구조

### 계층

`Controller → Service → DAO → Mapper XML(SQL)`

- Controller: 요청·응답, 입력 검증, 로그인 사용자 꺼내기
- Service: 로직, 트랜잭션
- DAO: `@Mapper`를 붙인 인터페이스입니다(예: `ClothesDao`). 구현 클래스와 `SqlSessionTemplate`은 쓰지 않고, `@MapperScan`도 같이 쓰지 않습니다. 파라미터가 2개 이상이면 `@Param`을 붙입니다.
- Mapper XML: `src/main/resources/mapper/{도메인}/` 아래(예: `ClothesMapper.xml`, `mapper-locations=classpath:mapper/*/*.xml`). MyBatis 동작 설정은 `mapper/mybatis_config.xml`이고 설정 클래스는 없습니다. 타입 별칭은 entity만(`com.mailook.*.entity`), dto는 전체 이름으로 씁니다. `namespace`는 DAO 인터페이스의 전체 이름(`com.mailook.closet.dao.ClothesDao`), SQL id는 DAO 메서드 이름과 똑같이 맞춥니다.
- Entity: 테이블 한 줄에 맞는 객체. DTO: 요청·응답 모양(`XxxCreateRequest`, `XxxResponse`)

### 반드시 지킬 것

- **다른 도메인의 DAO를 직접 부르지 않습니다.** 다른 도메인 데이터는 그 도메인의 Service로만 읽습니다. (예: 추천은 옷장 데이터를 `ClosetCandidateService`로만 읽음)
- **도메인 패키지 아래에 계층 패키지를 둡니다:** `controller`, `service`, `dao`, `entity`(테이블 한 줄 객체), `dto`, `enums`. 이 여섯 가지 외의 패키지는 만들지 않고, 필요하면 먼저 물어봅니다. 외부 API·계산 클래스(KmaClient, DeviationCalculator 등)는 `service`에 둡니다. **LLM을 부르는 코드는 도메인에 두지 않고 최상위 `ai` 패키지에 둡니다**(아래 '패키지' 참고).
- DAO와 XML은 테이블 묶음 단위로 하나씩 짝을 맞춥니다. 부모 테이블과 딸린 테이블은 같은 DAO·XML이 맡습니다.
  - PlaceDao / PlaceMapper.xml: places, place_schedules
  - ClothesDao / ClothesMapper.xml: clothes, clothes_occasion_tags
  - WearRecordDao / WearRecordMapper.xml: wear_records, wear_record_items
  - RecommendationDao / RecommendationMapper.xml: recommendations, recommendation_items, recommendation_weathers
  - FeedbackDao / FeedbackMapper.xml: feedbacks, feedback_body_parts, feedback_weather_factors, feedback_day_contexts
  - 나머지는 테이블 하나에 DAO·XML 하나

### 패키지

```
src/main/java/com/mailook/
├─ global/       response, exception, security, config, enums (공통 응답·예외·에러 코드, Security·JWT, 비동기 설정, 공용 ENUM)
├─ ai/           LLM 호출 모음: config(AiConfig, 기능별 ChatClient), classify(옷 분류), recommend(LLM 추천), preference(성향 요약·갱신 도구)
├─ user/         회원·설정
├─ place/        장소·동선 (RouteService)
├─ closet/       옷·보관 위치·후보 조회 (ClosetCandidateService)
├─ wear/         착용 기록
├─ recommend/    추천·챙길 것·환절기
├─ weather/      날씨·바깥 시간 목록 (OutdoorMomentService)
├─ warmth/       보온 계산 공통 모듈 (WarmthCalculator)
├─ feedback/     피드백·편차 계산
└─ preference/   성향·성향 갱신 도구
```

각 도메인 패키지 안은 `controller/`, `service/`, `dao/`, `entity/`, `dto/`, `enums/`로 나눕니다(예: `com.mailook.closet.dao.ClothesDao`). 파일이 없는 계층 패키지는 만들지 않습니다(예: `warmth`는 `service`, `dto`만).

`ai` 패키지는 도메인이 아니라 LLM 호출만 모은 곳입니다.

| 패키지 | 클래스 | 담당 |
|---|---|---|
| `ai/config` | AiConfig(기능별 ChatClient Bean) | 전이레 |
| `ai/classify` | ClothesClassifier | 최수인 |
| `ai/recommend` | LlmRecommender | 강민주 |
| `ai/preference` | PreferenceSummarizer, PreferenceUpdateTool | 전이레 |

- 기능 폴더 안에는 LLM을 부르는 클래스와, 그 기능의 LLM 응답 형식(구조화 출력용 record/DTO)만 둡니다. 하위 계층 패키지(controller·service·dao)는 만들지 않습니다.
- `ai`는 도메인 Service만 부릅니다(예: `ClothesService` → `ClothesClassifier`). Controller가 `ai`를 직접 부르지 않습니다.
- `ai` 안에서는 DAO를 부르지 않습니다. 필요한 데이터는 부르는 Service가 넘겨 주고, 결과 저장도 그 Service가 합니다. Tool Calling(`PreferenceUpdateTool`)이 값을 바꿀 때도 `PreferenceService`를 부릅니다.
- 보온·보정 계산은 `ai`에서 하지 않고, 검사가 필요하면 `WarmthCalculator`를 부릅니다.

한 도메인만 쓰는 ENUM은 그 도메인의 `enums`에, 여러 도메인이 쓰는 ENUM은 `global/enums`에 둡니다. `enum`은 자바 예약어라 패키지 이름으로 쓸 수 없어서 `enums`입니다.

## 5. API 규칙

- 주소는 모두 `/api`로 시작합니다.
- 응답은 공통 틀로 감쌉니다: `{ success, data, error: { code, message } }`
- 회원가입·로그인을 빼고 모두 로그인이 필요합니다. **JWT는 쿠키가 아니라 `Authorization: Bearer {토큰}` 헤더**로 받습니다. 로그인 API는 토큰을 응답 `data`의 `accessToken`으로 돌려주고, `JwtAuthenticationFilter`는 이 헤더에서 토큰을 꺼냅니다. 쿠키를 굽지 않으므로 CORS에서 `Authorization` 헤더만 허용하면 되고 credentials는 켜지 않습니다. Refresh Token은 설계에만 있고 구현하지 않습니다.
- **주소에 userId를 넣지 않습니다.** 토큰에서 꺼낸 로그인 사용자로 본인 데이터만 다룹니다.
- 엔드포인트 목록과 응답 예시는 Notion "API 명세 초안"을 따릅니다.

## 6. DB 규칙

- 테이블 정의는 `src/main/resources/schema.sql` 하나입니다(ERD에서 만든 PostgreSQL DDL). 테이블·컬럼 이름과 타입은 이 파일을 기준으로 Mapper XML을 씁니다.
- ENUM 컬럼에는 CHECK 제약이 걸려 있습니다. enum 값을 추가하거나 바꾸면 schema.sql의 CHECK도 같이 바꿔야 하므로, 먼저 물어봅니다.
- id는 BIGSERIAL입니다. clothes는 `<selectKey order="BEFORE">SELECT nextval('clothes_clothes_id_seq')</selectKey>`로 id를 먼저 받습니다.

- **삭제는 모두 soft delete**입니다. 행을 지우지 않고 `deleted_at`을 채웁니다. 조회 SQL에는 `deleted_at IS NULL` 조건을 꼭 넣습니다.
  - 예외: 탈퇴 회원은 30일 뒤 배치로 연결 데이터까지 완전 삭제합니다. 그 30일 동안 같은 이메일로 재가입할 수 없습니다.
  - 피드백 전 착용 기록 삭제와 옷 상황 태그 떼기를 soft로 할지는 아직 정하지 않았습니다(담당: 최수인).
- 보관 위치를 지우면 그 위치에 있던 옷의 `storage_location_id`를 null로 비웁니다(= "지정 없음").
- places, storage_locations의 이름에는 UNIQUE를 걸지 않습니다.
- 1:1 테이블(user_settings, preferences)은 `user_id`가 PK 겸 FK입니다. 회원가입 트랜잭션에서 users insert 직후 같이 만듭니다.
- 같은 옷 여러 벌은 한 벌에 한 줄이고 `same_group_id`로 묶습니다. 새 옷은 `<selectKey order="BEFORE">`로 id를 먼저 받아 `clothes_id`와 `same_group_id`에 같은 값을 넣습니다.
- 착용 기록은 하루 1개입니다. 피드백 전에는 덮어쓰고, **피드백이 저장되면 잠깁니다**(수정·삭제 불가).
- **추천은 새벽 배치가 미리 만듭니다.** 아침에 오늘 상황·몸 상태·오늘 동선이 바뀌면 홈이 안내를 띄우고, 사용자가 [새로 추천받기]를 누르면 그때 새 버전을 만듭니다(`POST /api/recommendations`).
- 추천은 **버전으로 쌓습니다.** "다른 조합 보기"나 재추천은 새 줄이고, 이전 추천을 고치지 않습니다. 고르지 않은 버전도 지우지 않습니다(비선호 데이터). ‘바꾸기’(P1)로 자리를 바꾼 조합도 `recommendation_items`를 고치지 않고, 착용 기록(`wear_record_items`)에 실제 입은 옷으로 저장합니다.
- **새벽 배치(05시대)**: 오늘 추천 생성(강민주). 기본값은 오늘 상황 = 그날 장소의 실내·야외 설정, 몸 상태 = `user_settings.unwell`, 동선 = 원래 일정입니다.
- 환절기 카드(`season_alerts`, P1)는 전날 배치가 만들고 홈은 읽기만 합니다. ‘꺼냈어요’면 DONE + 그 옷 `storage_status = AVAILABLE`.
- **날씨 모의 데이터**: `WeatherClient` 인터페이스 하나에 지금은 `MockWeatherClient`(랜덤)를 구현해 씁니다. 나중에 기상청 구현으로 바꿔도 다른 코드는 고치지 않게 합니다.
  - 랜덤은 (날짜, 격자 nx·ny, 시각, 예보/관측) 값으로 시드를 정해, **같은 조건이면 늘 같은 값**이 나오게 합니다. 같은 날 다시 불러도 추천·피드백이 어긋나지 않고, 날마다 다른 날씨가 나옵니다.
  - 기온은 그 달에 나올 만한 범위에서 고르고, 하루 안에서는 아침이 낮보다 춥게 만듭니다. 관측값은 예보값에서 조금(±2° 안) 다르게 만듭니다.
  - 발표용으로 `weather.mock.scenario`(`RANDOM`/`COLD`/`HOT`/`RAINY`/`WINDY`) 설정을 두어 원하는 날씨를 바로 보여줄 수 있게 합니다. 기본은 `RANDOM`입니다.
- 날씨 스냅샷(`weather_snapshots`)은 기상청 격자·시각 단위로 공유하고, **덮어쓰지도 지우지도 않습니다.** 같은 (nx, ny, forecast_at, kind)의 최신 값과 같으면 새 줄을 만들지 않고 재사용합니다.
- 장소 주소는 동 단위까지만 저장하고, 위경도는 소수점 둘째 자리로 반올림해 저장합니다.

### ENUM

- DB는 varchar이고, **MyBatis 기본 `EnumTypeHandler`**가 enum 이름 ↔ 문자열을 바꿉니다. 별도 TypeHandler를 만들지 않습니다.
- enum 상수 이름을 바꾸면 이미 저장된 데이터를 읽지 못합니다. 이름을 바꿔야 하면 DB 값도 같이 바꿉니다.
- 순서 번호(ordinal) 방식은 쓰지 않습니다.
- XML `test` 조건에서 enum을 문자열과 비교할 때는 `category != null and category.name() == 'OUTER'`처럼 씁니다.
- 테스트 데이터의 enum 값은 대소문자까지 정확히 맞춥니다.

## 7. 핵심 계산 (보온·보정)

**계산은 코드만 합니다. LLM은 숫자를 계산하지 않습니다.** 보온 계산은 `warmth` 공통 모듈(`WarmthCalculator.calcOutfitWarmth`) 하나로 하고, 추천과 피드백이 같은 모듈을 씁니다. 각자 따로 구현하지 않습니다.

- 조합 clo = 0.835 × (입은 옷 clo 합) + 0.16  (ISO 9920. 입는 옷 WEAR만 넣고, 챙길 것 BRING은 넣지 않음)
- 적정 clo = (31 − 기준 온도) ÷ (0.155 × 활동량)
  - 기준 온도 = 체감 − 보정 × 적용 비율
  - 활동량: 바깥 115, 실내 60
  - 최소 0.36(반팔+반바지). 그보다 더운 날은 통풍 높은 옷을 먼저 고릅니다.
- 적용 비율 = min(1, |체감 − 20| ÷ 4)
- 보정은 두 개입니다. 체감 20° 미만인 시점은 추위 보정(cold_offset), 20° 이상은 더위 보정(heat_offset). 범위 −5 ~ +5°, +면 따뜻하게.
- 화면의 따뜻한 정도 10단계 = round(clo ÷ 0.08)(0 이하는 1). 이 변환은 **프론트가** 합니다. 백엔드는 clo만 주고받고 단계(warmthLevel)를 만들지 않습니다.
- 실내 검사: 아우터·액세서리를 뺀 조합이 장소 실내 온도(HOT 26° / NORMAL 22° / COLD 18°)와 ±3° 안이면 통과.
- "오늘은 몸이 안 좋아요"(unwell): 켜진 상태는 `user_settings.unwell`(끌 때까지 유지), 다시 물어본 날은 `user_settings.unwell_asked_on`에 저장하고, 추천을 만들 때 `recommendations.unwell`로 복사합니다. 그날 기준 온도를 4° 낮춰 계산하고, 그날 피드백은 학습에서 뺍니다.

### 바깥 시간 목록 (OutdoorMomentService)

- 장소 일정으로 그날 밖에 있는 순간을 만듭니다: DEPART(집 출발), MOVE(장소 간 이동), STAY_OUTDOOR(OUTDOOR·MIXED 장소에 머무는 동안), RETURN(귀가), DAYTIME(일정 없는 날, 집 근처 12~18시).
- 시각: DEPART = 집 `depart_time`, MOVE = 떠나는 장소의 `leave_time`, RETURN = 그날 마지막 활동 장소의 `leave_time`. 집의 `arrive_time`(돌아오는 시간)은 동선 표시용이고 계산에 쓰지 않습니다.
- DEPART·MOVE·RETURN은 있던 곳과 가는 곳의 날씨를 둘 다 조회하고, 체감 20° 미만이면 더 추운 곳, 20° 이상이면 더 더운 곳 값을 그 시점 값으로 씁니다. 화면 표시는 "집 → 직장"처럼 있던 곳 → 가는 곳입니다.
- STAY_OUTDOOR와 DAYTIME은 그 시간 중 가장 시원한 시각과 가장 더운 시각, 두 줄을 남깁니다.
- **기준 시점**(`is_deciding`)은 하루에 하나입니다. 체감 20° 미만인 시점이 하나라도 있으면 가장 추운 시점(적정 clo가 가장 높은 시점), 모든 시점이 20° 이상이면 가장 더운 시점입니다. 옷은 이 시점에 맞춰 고르고, 피드백 보정도 이 시점 체감으로 계산합니다.
- `build(userId, date)`는 `RouteService.getTodayRoute`로 동선을 가져오고, `build(userId, date, route)`는 넘겨받은 동선을 씁니다. ‘오늘 동선 바꾸기’(REQ-412)에서 추천 쪽이 덮어쓴 동선을 넘길 때 씁니다.
- 시점별 체감 차이가 기준(예: 10°) 이상이면 일교차가 크다고 보고, 겉옷을 입은 상태는 가장 추운 시점에, 벗은 상태는 가장 더운 시점에 맞춥니다.

### 피드백 → 보정

- 입은 조합이 맞는 온도 = 31 − 0.155 × 활동량 × 조합 clo
- 나에게 맞았던 온도 = 맞는 온도 − 4° × 선호 점수(더 따뜻하게 +1 / 이대로 0 / 더 가볍게 −1)
- 이 피드백의 보정 = (기준 시점 체감 − 나에게 맞았던 온도) ÷ 적용 비율. 기준 시점 체감이 20° 미만이면 추위 보정, 이상이면 더위 보정으로 보냅니다.
- 제외: 기준 시점 체감 18~22°(적용 비율 0.5 미만), unwell인 날, "오늘 안 나갔어요"인 날
- 갱신: 같은 방향 3건 이상일 때만, 최근 10건의 중앙값으로, 한 번에 최대 1°까지 움직입니다. **피드백을 저장할 때 코드가 바로 계산해 반영합니다**(LLM을 거치지 않음). 그래서 내 성향 화면과 다음 추천에 바로 들어갑니다.
- 가입 초기값: 추위 덜 탐 −4 / 비슷 0 / 더 탐 +4, 더위 덜 탐 +4 / 비슷 0 / 더 탐 −4
- 가입 후 기본 설정에서 추위·더위 답을 고치면 `PreferenceService.resetOffset`으로 그 보정을 새 초기값으로 다시 맞추고 이력(USER)을 남깁니다.
- 사용자가 직접 고친 값은 `*_locked = true`이고, AI가 덮어쓰지 않습니다. 바뀔 때마다 `preference_histories`에 남깁니다.

## 8. LLM 사용 규칙

- LLM 호출 코드는 모두 `ai` 패키지에 둡니다. 기능별로 ChatClient를 나눕니다(옷 분류, 추천, 성향 요약).
- **추천**: 코드가 후보 옷을 걸러 넘기고, LLM은 후보 안에서만 조합을 고릅니다. 후보 ID 밖의 응답은 거부합니다. LLM이 고른 조합은 `calcOutfitWarmth`로 검사하고, 맞지 않으면 이유를 붙여 2~3번 다시 요청하고, 그래도 안 되면 규칙 기반 추천으로 대신합니다.
- LLM에는 장소 이름·동 단위 주소·머무는 시간, 옷의 상황 태그(외출복/출근용/운동복/격식 있는 자리, 없으면 일상), 반영 중인 유저 성향 문장을 맥락으로 넘깁니다. 상황 태그는 코드로 거르지 않습니다.
- **피드백 저장**: 저장·기준 시점 관측 날씨 기록(`feedbacks.moment`·`place_id`·`weather_snapshot_id`, 시점별 테이블 없음)·편차 계산·추위·더위 보정 갱신까지 요청 안에서 코드로 끝냅니다. 저장 응답은 LLM을 기다리지 않습니다.
- **성향 정리(`@Async`)**: 피드백 저장이 끝난(커밋된) 뒤 Controller가 `PreferenceSummaryService.summarizeAsync(userId)`를 부릅니다. LLM이 최근 피드백을 보고 바람·습도·햇볕 민감도와 유저 성향 문장을 정리합니다(`updatePreference` Tool Calling). 추위·더위 보정 숫자는 LLM이 바꾸지 않습니다.
  - LLM에 넘기는 것: 지금 성향 문장(근거 건수)·민감도, 코드가 집계한 **최근 10건** 통계(부위·날씨 요인·그날 상황을 고른 횟수와 비율), `summarized_at` 이후 새 피드백 원문(남길 말 포함). 지난 피드백 원문을 전부 넘기지 않습니다. 민감도 비율도 추위·더위 보정처럼 최근 10건으로 셉니다.
  - 성공하면 `preferences.summarized_at`을 지금 시각으로 바꿉니다. 상태 컬럼은 따로 두지 않습니다. 예외는 `@Async` 메서드 안에서 `try`/`catch`로 잡아 로그만 남깁니다(밖으로 던지면 아무도 받지 못합니다). 실패하면 값이 그대로라 다음 피드백 때 다시 정리됩니다.
  - `@Async` 메서드는 **다른 Bean**에 둡니다. 같은 클래스 안에서 부르면 비동기로 돌지 않습니다. `@EnableAsync`와 스레드 풀은 `global/config/AsyncConfig`에 둡니다.
  - '오늘 안 나갔어요'(저장 안 함)와 몸 상태가 안 좋은 날(학습 제외) 피드백은 성향 정리를 부르지 않습니다.
  - 완료 안내: `GET /api/preferences/me` 응답에 `summarizedAt`을 넣습니다. 프론트가 localStorage에 둔 마지막 값과 비교해, 더 최신이면 "성향 정리가 끝났어요" 토스트를 띄웁니다. 안내용 API·컬럼은 따로 만들지 않습니다.
- 사용자가 남긴 말(memo)은 프롬프트에서 **지시가 아니라 데이터로만** 다룹니다.
- 응답은 구조화 출력(정해진 JSON 형식)으로 받습니다. 챗봇처럼 자유 대화로 쓰지 않습니다.

## 9. 하지 말 것

- JPA, Repository를 추가하지 않습니다.
- 다른 도메인의 DAO를 직접 주입하지 않습니다.
- DAO 구현 클래스나 `SqlSessionTemplate` 직접 호출을 만들지 않습니다. DAO는 `@Mapper` 인터페이스로 씁니다.
- LLM에게 clo·보정 계산을 맡기지 않습니다.
- 보온 계산을 추천과 피드백에서 따로 구현하지 않습니다.
- 날씨 스냅샷과 지난 추천 버전을 덮어쓰거나 지우지 않습니다.
- 행을 실제로 DELETE하지 않습니다(탈퇴 배치 제외).
- 상세 주소나 정확한 위경도를 저장하지 않습니다.
- 비밀번호·키를 코드나 문서에 적지 않습니다. 환경변수로 받습니다(BCrypt로 비밀번호 저장).
- main, develop 브랜치에 직접 커밋하지 않습니다.
- 프론트 폴더의 파일을 고치지 않습니다. 필요하면 무엇이 필요한지 작업 결과에 적습니다.