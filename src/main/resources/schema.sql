
-- DROP DATABASE IF EXISTS discodeit;
-- CREATE DATABASE discodeit ENCODING = 'UTF8';

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

-- binary_contents 테이블
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        bytea        NOT NULL,
    created_at   timestamptz  NOT NULL DEFAULT NOW()
);

-- users 테이블
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(60) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    profile_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NULL,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- channels 테이블
CREATE TABLE channels (
    id          uuid PRIMARY KEY,
    type        VARCHAR(10)  NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE')),
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    created_at  timestamptz  NOT NULL DEFAULT NOW(),
    updated_at  timestamptz  NULL
);

-- messages 테이블
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    content    TEXT        NOT NULL,
    channel_id uuid        NOT NULL,
    author_id  uuid        NULL,
    created_at timestamptz NOT NULL DEFAULT NOW(),
    updated_at timestamptz NULL,
    CONSTRAINT fk_msg_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);


-- read_statuses 테이블 (유저-채널)
CREATE TABLE read_statuses (
    id           uuid PRIMARY KEY,
    user_id      uuid        NOT NULL,
    channel_id   uuid        NOT NULL,
    last_read_at timestamptz NOT NULL,
    created_at   timestamptz NOT NULL DEFAULT NOW(),
    updated_at   timestamptz NULL,
    CONSTRAINT fk_rs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ,
    CONSTRAINT fk_rs_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);


-- user_statuses 테이블
CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    user_id        uuid        NOT NULL,
    last_active_at timestamptz NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT NOW(),
    updated_at     timestamptz NULL,
    CONSTRAINT fk_us_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


-- message_attachments 테이블
CREATE TABLE message_attachments
(
    message_id uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_ma_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_ma_binary FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);