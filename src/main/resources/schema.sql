-- ============================
-- 🚀 binary_contents 테이블
-- ============================
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    bytes        BYTEA                    NOT NULL,
    message_id   UUID
);

-- 메시지 삭제 시 첨부파일도 삭제
ALTER TABLE binary_contents
    ADD CONSTRAINT fk_binarycontent_message
        FOREIGN KEY (message_id) REFERENCES messages (id)
            ON DELETE CASCADE;

ALTER TABLE binary_contents
    OWNER TO discodeit_user;

-- ============================
-- 🚀 users 테이블
-- ============================
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID,
    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id) REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

ALTER TABLE users
    OWNER TO discodeit_user;

-- ============================
-- 🚀 channels 테이블
-- ============================
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL
);

ALTER TABLE channels
    OWNER TO discodeit_user;

-- ============================
-- 🚀 channel_participants (N:N)
-- ============================
CREATE TABLE channel_participants
(
    channel_id UUID NOT NULL,
    user_id    UUID NOT NULL,
    PRIMARY KEY (channel_id, user_id),
    CONSTRAINT fk_participant_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ============================
-- 🚀 messages 테이블
-- ============================
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT,
    channel_id UUID                     NOT NULL,
    author_id  UUID,
    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

ALTER TABLE messages
    OWNER TO discodeit_user;

-- ============================
-- 🚀 user_statuses 테이블
-- ============================
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    user_id        UUID                     NOT NULL UNIQUE,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

ALTER TABLE user_statuses
    OWNER TO discodeit_user;

-- ============================
-- 🚀 read_statuses 테이블
-- ============================
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    user_id      UUID                     NOT NULL,
    channel_id   UUID                     NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_read_status_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uq_read_status UNIQUE (user_id, channel_id)
);

ALTER TABLE read_statuses
    OWNER TO discodeit_user;
