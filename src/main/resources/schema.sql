-- =========================
-- 0) ENUM 타입 정의
-- =========================
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'channel_type') THEN
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');
END IF;
END $$;

-- =========================
-- 1) 테이블 생성
-- =========================

-- binary_contents
CREATE TABLE IF NOT EXISTS binary_contents (
                                               id            UUID         PRIMARY KEY,
                                               created_at    TIMESTAMPTZ  NOT NULL,
                                               file_name     VARCHAR(255) NOT NULL,
    size          BIGINT       NOT NULL,
    content_type  VARCHAR(100) NOT NULL,
    bytes         BYTEA        NOT NULL
    );

-- users
CREATE TABLE IF NOT EXISTS users (
                                     id          UUID          PRIMARY KEY,
                                     created_at  TIMESTAMPTZ   NOT NULL,
                                     updated_at  TIMESTAMPTZ,
                                     username    VARCHAR(50)   NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    password    VARCHAR(60)   NOT NULL,
    profile_id  UUID,
    CONSTRAINT uq_users_email    UNIQUE (email),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT fk_users_profile_id
    FOREIGN KEY (profile_id)
    REFERENCES binary_contents (id)
    ON DELETE SET NULL
    );

-- channels
CREATE TABLE IF NOT EXISTS channels (
                                        id           UUID          PRIMARY KEY,
                                        created_at   TIMESTAMPTZ   NOT NULL,
                                        updated_at   TIMESTAMPTZ,
                                        name         VARCHAR(100),
    description  VARCHAR(500),
    type         channel_type  NOT NULL
    );

-- messages
CREATE TABLE IF NOT EXISTS messages (
                                        id          UUID          PRIMARY KEY,
                                        created_at  TIMESTAMPTZ   NOT NULL,
                                        updated_at  TIMESTAMPTZ,
                                        content     TEXT,
                                        channel_id  UUID          NOT NULL,
                                        author_id   UUID,
                                        CONSTRAINT fk_messages_channel
                                        FOREIGN KEY (channel_id)
    REFERENCES channels (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_messages_author
    FOREIGN KEY (author_id)
    REFERENCES users (id)
    ON DELETE SET NULL
    );

-- user_statuses
CREATE TABLE IF NOT EXISTS user_statuses (
                                             id             UUID          PRIMARY KEY,
                                             created_at     TIMESTAMPTZ   NOT NULL,
                                             updated_at     TIMESTAMPTZ,
                                             user_id        UUID          NOT NULL,
                                             last_active_at TIMESTAMPTZ   NOT NULL,
                                             CONSTRAINT uq_user_statuses_user UNIQUE (user_id),
    CONSTRAINT fk_user_statuses_user
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE
    );

-- read_statuses
CREATE TABLE IF NOT EXISTS read_statuses (
                                             id           UUID          PRIMARY KEY,
                                             created_at   TIMESTAMPTZ   NOT NULL,
                                             updated_at   TIMESTAMPTZ,
                                             user_id      UUID          NOT NULL,
                                             channel_id   UUID          NOT NULL,
                                             last_read_at TIMESTAMPTZ   NOT NULL,
                                             CONSTRAINT uq_read_statuses_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_statuses_user
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel
    FOREIGN KEY (channel_id)
    REFERENCES channels (id)
    ON DELETE CASCADE
    );

-- message_attachments (PK 없음, FK 2개만)
CREATE TABLE IF NOT EXISTS message_attachments (
                                                   message_id    UUID NOT NULL,
                                                   attachment_id UUID NOT NULL,
                                                   CONSTRAINT fk_msg_attach_message
                                                   FOREIGN KEY (message_id)
    REFERENCES messages (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_msg_attach_attachment
    FOREIGN KEY (attachment_id)
    REFERENCES binary_contents (id)
    ON DELETE CASCADE
    );

-- =========================
-- 2) 보조 인덱스
-- =========================
CREATE INDEX IF NOT EXISTS idx_users_profile_id                ON users (profile_id);
CREATE INDEX IF NOT EXISTS idx_messages_channel_id             ON messages (channel_id);
CREATE INDEX IF NOT EXISTS idx_messages_author_id              ON messages (author_id);
CREATE INDEX IF NOT EXISTS idx_user_statuses_user_id           ON user_statuses (user_id);
CREATE INDEX IF NOT EXISTS idx_read_statuses_user_id           ON read_statuses (user_id);
CREATE INDEX IF NOT EXISTS idx_read_statuses_channel_id        ON read_statuses (channel_id);
CREATE INDEX IF NOT EXISTS idx_message_attachments_message_id  ON message_attachments (message_id);
CREATE INDEX IF NOT EXISTS idx_message_attachments_attachment_id ON message_attachments (attachment_id);
