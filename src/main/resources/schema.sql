-- mAILOOK P0/P1 schema, PostgreSQL 17. Target: postgres.public.

-- P2 uniforms/uniform_items/uniform_days and vector embedding are intentionally deferred.

-- datetime -> timestamp without time zone; tinyint -> smallint; enum -> varchar + CHECK.

-- created_at/updated_at are supplied by the application; updated_at has no automatic trigger.

-- Soft deletion and business rules (feedback locks, ownership, withdrawal cleanup) belong to services.

BEGIN;

CREATE SCHEMA IF NOT EXISTS public;

SET LOCAL search_path TO public;

CREATE TABLE users (
    user_id bigserial PRIMARY KEY,
    email varchar(100) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    nickname varchar(100) NOT NULL,
    status varchar(30) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DORMANT', 'WITHDRAWN')),
    withdrawn_at timestamp without time zone,
    location_agreed_at timestamp without time zone NOT NULL,
    last_login_at timestamp without time zone,
    last_logout_at timestamp without time zone,
    body_photo_url varchar(500),
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE user_settings (
    user_id bigint PRIMARY KEY,
    warmth_style varchar(30) NOT NULL DEFAULT 'ANY' CHECK (warmth_style IN ('INNER_LAYER', 'OUTER_LAYER', 'ANY')),
    cold_sensitivity varchar(30) NOT NULL DEFAULT 'NORMAL' CHECK (cold_sensitivity IN ('LESS', 'NORMAL', 'MORE')),
    heat_sensitivity varchar(30) NOT NULL DEFAULT 'NORMAL' CHECK (heat_sensitivity IN ('LESS', 'NORMAL', 'MORE')),
    unwell boolean NOT NULL DEFAULT false,
    unwell_asked_on date,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE places (
    place_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    place_type varchar(30) NOT NULL CHECK (place_type IN ('HOME', 'ACTIVITY')),
    name varchar(30) NOT NULL,
    address varchar(100) NOT NULL,
    latitude decimal(5,2) NOT NULL,
    longitude decimal(5,2) NOT NULL,
    depart_time time,
    arrive_time time,
    leave_time time,
    environment varchar(30) CHECK (environment IN ('INDOOR', 'OUTDOOR', 'MIXED')),
    indoor_temp varchar(30) CHECK (indoor_temp IN ('HOT', 'NORMAL', 'COLD')),
    deleted_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE place_schedules (
    place_id bigint NOT NULL,
    day_of_week varchar(30) NOT NULL CHECK (day_of_week IN ('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN')),
    deleted_at timestamp without time zone,
    PRIMARY KEY (place_id, day_of_week)
);

CREATE TABLE storage_locations (
    storage_location_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    name varchar(30) NOT NULL,
    deleted_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE clothes (
    clothes_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    storage_location_id bigint,
    name varchar(50) NOT NULL,
    category varchar(30) NOT NULL CHECK (category IN ('OUTER', 'TOP', 'BOTTOM', 'SHOES', 'ACCESSORY', 'CARRY_ITEM')),
    sub_type varchar(30),
    material varchar(30) CHECK (material IN ('WOOL', 'COTTON', 'POLYESTER', 'DOWN', 'LINEN', 'DENIM', 'ETC')),
    clo decimal(3,2),
    neck_cover boolean NOT NULL DEFAULT false,
    breathability smallint,
    storage_status varchar(30) NOT NULL DEFAULT 'AVAILABLE' CHECK (storage_status IN ('AVAILABLE', 'STORED')),
    image_url varchar(500) NOT NULL,
    user_edited boolean NOT NULL DEFAULT false,
    same_group_id bigint NOT NULL,
    deleted_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE clothes_occasion_tags (
    clothes_id bigint NOT NULL,
    tag varchar(30) NOT NULL CHECK (tag IN ('OUTING', 'WORK', 'SPORTS', 'FORMAL')),
    PRIMARY KEY (clothes_id, tag)
);

CREATE TABLE wear_records (
    wear_record_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    recommendation_id bigint,
    worn_date date NOT NULL,
    situation varchar(30) NOT NULL CHECK (situation IN ('INDOOR', 'OUTDOOR', 'MOVING', 'FORMAL')),
    created_at timestamp without time zone NOT NULL,
    UNIQUE (user_id, worn_date)
);

CREATE TABLE wear_record_items (
    wear_record_id bigint NOT NULL,
    clothes_id bigint NOT NULL,
    PRIMARY KEY (wear_record_id, clothes_id)
);

CREATE TABLE recommendations (
    recommendation_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    recommend_date date NOT NULL,
    situation varchar(30) NOT NULL CHECK (situation IN ('INDOOR', 'OUTDOOR', 'MOVING', 'FORMAL')),
    reason text NOT NULL,
    unwell boolean NOT NULL DEFAULT false,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE recommendation_weathers (
    recommendation_id bigint NOT NULL,
    seq smallint NOT NULL,
    moment varchar(30) NOT NULL CHECK (moment IN ('DEPART', 'MOVE', 'STAY_OUTDOOR', 'RETURN', 'DAYTIME')),
    place_id bigint NOT NULL,
    weather_snapshot_id bigint NOT NULL,
    required_clo decimal(3,2) NOT NULL,
    is_deciding boolean NOT NULL DEFAULT false,
    PRIMARY KEY (recommendation_id, seq)
);

CREATE TABLE recommendation_items (
    recommendation_id bigint NOT NULL,
    clothes_id bigint NOT NULL,
    role varchar(30) NOT NULL DEFAULT 'WEAR' CHECK (role IN ('WEAR', 'BRING')),
    reason varchar(255),
    PRIMARY KEY (recommendation_id, clothes_id)
);

CREATE TABLE season_alerts (
    season_alert_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    clothes_id bigint NOT NULL,
    message varchar(255) NOT NULL,
    alert_date date NOT NULL,
    status varchar(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'DONE', 'LATER')),
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE weather_snapshots (
    weather_snapshot_id bigserial PRIMARY KEY,
    nx int NOT NULL,
    ny int NOT NULL,
    forecast_at timestamp without time zone NOT NULL,
    kind varchar(30) NOT NULL CHECK (kind IN ('FORECAST', 'OBSERVED')),
    base_at timestamp without time zone NOT NULL,
    temperature decimal(4,1) NOT NULL,
    feels_like decimal(4,1) NOT NULL,
    wind_speed decimal(4,1) NOT NULL,
    humidity int NOT NULL,
    precipitation_type varchar(20),
    sky varchar(20),
    created_at timestamp without time zone NOT NULL,
    UNIQUE (nx, ny, forecast_at, kind, base_at)
);

CREATE TABLE feedbacks (
    feedback_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    wear_record_id bigint NOT NULL UNIQUE,
    preference varchar(30) NOT NULL CHECK (preference IN ('WARMER', 'KEEP', 'LIGHTER')),
    sensation varchar(30) CHECK (sensation IN ('COLD', 'CHILLY', 'OK', 'HOT')),
    memo varchar(200),
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE feedback_weathers (
    feedback_id bigint NOT NULL,
    seq smallint NOT NULL,
    moment varchar(30) NOT NULL CHECK (moment IN ('DEPART', 'MOVE', 'STAY_OUTDOOR', 'RETURN', 'DAYTIME')),
    place_id bigint NOT NULL,
    weather_snapshot_id bigint NOT NULL,
    is_deciding boolean NOT NULL DEFAULT false,
    PRIMARY KEY (feedback_id, seq)
);

CREATE TABLE feedback_body_parts (
    feedback_id bigint NOT NULL,
    body_part varchar(30) NOT NULL CHECK (body_part IN ('NECK', 'BODY', 'HAND', 'LEG', 'FOOT')),
    PRIMARY KEY (feedback_id, body_part)
);

CREATE TABLE feedback_day_contexts (
    feedback_id bigint NOT NULL,
    day_context varchar(30) NOT NULL CHECK (day_context IN ('MOSTLY_INDOOR', 'SHORT_OUTDOOR', 'LONG_OUTDOOR', 'ACTIVE', 'INDOOR_HOT', 'INDOOR_COLD')),
    PRIMARY KEY (feedback_id, day_context)
);

CREATE TABLE feedback_weather_factors (
    feedback_id bigint NOT NULL,
    weather_factor varchar(30) NOT NULL CHECK (weather_factor IN ('WIND', 'HUMID', 'SUN', 'RAIN_SNOW')),
    PRIMARY KEY (feedback_id, weather_factor)
);

CREATE TABLE preferences (
    user_id bigint PRIMARY KEY,
    cold_offset smallint NOT NULL DEFAULT 0,
    heat_offset smallint NOT NULL DEFAULT 0,
    wind_sensitivity smallint NOT NULL DEFAULT 2,
    humid_sensitivity smallint NOT NULL DEFAULT 2,
    sun_sensitivity smallint NOT NULL DEFAULT 2,
    cold_offset_locked boolean NOT NULL DEFAULT false,
    heat_offset_locked boolean NOT NULL DEFAULT false,
    wind_locked boolean NOT NULL DEFAULT false,
    humid_locked boolean NOT NULL DEFAULT false,
    sun_locked boolean NOT NULL DEFAULT false,
    feedback_count int NOT NULL DEFAULT 0,
    summarized_at timestamp without time zone,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE preference_traits (
    preference_trait_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    content varchar(255) NOT NULL,
    evidence_count int NOT NULL DEFAULT 0,
    rejected boolean NOT NULL DEFAULT false,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE preference_histories (
    preference_history_id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    field varchar(30) NOT NULL,
    before_value varchar(30),
    after_value varchar(30) NOT NULL,
    changed_by varchar(30) NOT NULL CHECK (changed_by IN ('AI', 'USER')),
    reason varchar(255),
    created_at timestamp without time zone NOT NULL
);

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

ALTER TABLE feedbacks ADD CONSTRAINT fk_feedbacks_wear_record_id FOREIGN KEY (wear_record_id) REFERENCES wear_records (wear_record_id) ON DELETE RESTRICT;

ALTER TABLE feedback_weathers ADD CONSTRAINT fk_feedback_weathers_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;

ALTER TABLE feedback_weathers ADD CONSTRAINT fk_feedback_weathers_place_id FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE RESTRICT;

ALTER TABLE feedback_weathers ADD CONSTRAINT fk_feedback_weathers_weather_snapshot_id FOREIGN KEY (weather_snapshot_id) REFERENCES weather_snapshots (weather_snapshot_id) ON DELETE RESTRICT;

ALTER TABLE feedback_body_parts ADD CONSTRAINT fk_feedback_body_parts_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;

ALTER TABLE feedback_day_contexts ADD CONSTRAINT fk_feedback_day_contexts_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;

ALTER TABLE feedback_weather_factors ADD CONSTRAINT fk_feedback_weather_factors_feedback_id FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id) ON DELETE RESTRICT;

ALTER TABLE preferences ADD CONSTRAINT fk_preferences_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;

ALTER TABLE preference_traits ADD CONSTRAINT fk_preference_traits_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;

ALTER TABLE preference_histories ADD CONSTRAINT fk_preference_histories_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT;

CREATE INDEX idx_recommendations_user_id_recommend_date ON recommendations (user_id, recommend_date);

COMMIT;

