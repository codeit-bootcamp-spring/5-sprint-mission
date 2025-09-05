SELECT current_database(), current_user;

-- ===== ENUM: channel type =====
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'channel_type') THEN
            CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');
        END IF;
    END $$;

-- ===== 1) 바이너리 컨텐츠 (메타+바이너리) =====
CREATE TABLE IF NOT EXISTS binary_contents (
                                               id            UUID PRIMARY KEY,
                                               created_at    TIMESTAMPTZ NOT NULL,
                                               file_name     VARCHAR(255) NOT NULL,
                                               size          BIGINT       NOT NULL,
                                               content_type  VARCHAR(100) NOT NULL,
                                               bytes         BYTEA        NOT NULL
);

-- ===== 2) 유저 =====
CREATE TABLE IF NOT EXISTS users (
                                     id           UUID PRIMARY KEY,
                                     created_at   TIMESTAMPTZ NOT NULL,
                                     updated_at   TIMESTAMPTZ,
                                     username     VARCHAR(50)  NOT NULL,
                                     email        VARCHAR(100) NOT NULL,
                                     password     VARCHAR(60)  NOT NULL,
                                     profile_id   UUID,
                                     CONSTRAINT uk_users_username UNIQUE (username),
                                     CONSTRAINT uk_users_email    UNIQUE (email),
                                     CONSTRAINT fk_users_profile
                                         FOREIGN KEY (profile_id)
                                             REFERENCES binary_contents(id)
                                             ON DELETE SET NULL
);

-- ===== 3) 유저 상태 (1:1, 유저 삭제 시 같이 삭제) =====
CREATE TABLE IF NOT EXISTS user_statuses (
                                             id             UUID PRIMARY KEY,
                                             created_at     TIMESTAMPTZ NOT NULL,
                                             updated_at     TIMESTAMPTZ,
                                             user_id        UUID        NOT NULL UNIQUE,
                                             last_active_at TIMESTAMPTZ NOT NULL,
                                             CONSTRAINT fk_user_statuses_user
                                                 FOREIGN KEY (user_id)
                                                     REFERENCES users(id)
                                                     ON DELETE CASCADE
);

-- ===== 4) 채널 =====
CREATE TABLE IF NOT EXISTS channels (
                                        id          UUID PRIMARY KEY,
                                        created_at  TIMESTAMPTZ NOT NULL,
                                        updated_at  TIMESTAMPTZ,
                                        name        VARCHAR(100)  NOT NULL,
                                        description VARCHAR(500),
                                        type        channel_type  NOT NULL
);

-- ===== 5) 메시지 =====
CREATE TABLE IF NOT EXISTS messages (
                                        id         UUID PRIMARY KEY,
                                        created_at TIMESTAMPTZ NOT NULL,
                                        updated_at TIMESTAMPTZ,
                                        content    TEXT,
                                        channel_id UUID NOT NULL,
                                        author_id  UUID,
                                        CONSTRAINT fk_messages_channel
                                            FOREIGN KEY (channel_id)
                                                REFERENCES channels(id)
                                                ON DELETE CASCADE,
                                        CONSTRAINT fk_messages_author
                                            FOREIGN KEY (author_id)
                                                REFERENCES users(id)
                                                ON DELETE SET NULL
);

-- 성능 인덱스(권장)
CREATE INDEX IF NOT EXISTS idx_messages_channel_id ON messages(channel_id);
CREATE INDEX IF NOT EXISTS idx_messages_author_id  ON messages(author_id);

-- ===== 6) 읽음 상태 (user+channel 유니크) =====
CREATE TABLE IF NOT EXISTS read_statuses (
                                             id           UUID PRIMARY KEY,
                                             created_at   TIMESTAMPTZ NOT NULL,
                                             updated_at   TIMESTAMPTZ,
                                             user_id      UUID        NOT NULL,
                                             channel_id   UUID        NOT NULL,
                                             last_read_at TIMESTAMPTZ NOT NULL,
                                             CONSTRAINT fk_read_statuses_user
                                                 FOREIGN KEY (user_id)
                                                     REFERENCES users(id)
                                                     ON DELETE CASCADE,
                                             CONSTRAINT fk_read_statuses_channel
                                                 FOREIGN KEY (channel_id)
                                                     REFERENCES channels(id)
                                                     ON DELETE CASCADE,
                                             CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

CREATE INDEX IF NOT EXISTS idx_read_statuses_user_id    ON read_statuses(user_id);
CREATE INDEX IF NOT EXISTS idx_read_statuses_channel_id ON read_statuses(channel_id);

-- ===== 7) 메시지 첨부 (다대다 조인) =====
CREATE TABLE IF NOT EXISTS message_attachments (
                                                   message_id    UUID NOT NULL,
                                                   attachment_id UUID NOT NULL,
                                                   PRIMARY KEY (message_id, attachment_id),
                                                   CONSTRAINT fk_msg_att_message
                                                       FOREIGN KEY (message_id)
                                                           REFERENCES messages(id)
                                                           ON DELETE CASCADE,
                                                   CONSTRAINT fk_msg_att_attachment
                                                       FOREIGN KEY (attachment_id)
                                                           REFERENCES binary_contents(id)
                                                           ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_message_attachments_attachment_id
    ON message_attachments(attachment_id);
