-- DROP TABLES (존재 시 삭제)
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- ENUM 생성
DO $$ BEGIN
    CREATE TYPE type_enum AS ENUM ('PRIVATE', 'PUBLIC');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- binary_contents
CREATE TABLE binary_contents (
                                 id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                 file_name    VARCHAR(255) NOT NULL,
                                 size         BIGINT       NOT NULL,
                                 content_type VARCHAR(100) NOT NULL,
                                 bytes        BYTEA        NOT NULL
);

-- users
CREATE TABLE users (
                       id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username   VARCHAR(50)  NOT NULL UNIQUE,
                       password   VARCHAR(60) NOT NULL,
                       email      VARCHAR(100) NOT NULL UNIQUE,
                       profile_id UUID UNIQUE,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                       updated_at TIMESTAMPTZ DEFAULT now(),
                       CONSTRAINT fk_users_binary_contents
                           FOREIGN KEY (profile_id)
                               REFERENCES binary_contents (id)
                               ON DELETE SET NULL
);

-- channels
CREATE TABLE channels (
                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name        VARCHAR(100),
                          description VARCHAR(500),
                          type        type_enum NOT NULL,
                          created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at  TIMESTAMPTZ DEFAULT now()
);

-- messages
CREATE TABLE messages (
                          id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          content    TEXT NOT NULL,
                          channel_id UUID NOT NULL,
                          author_id  UUID,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ DEFAULT now(),
                          CONSTRAINT fk_channels_messages
                              FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_author
                              FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- user_statuses
CREATE TABLE user_statuses (
                               id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                               updated_at     TIMESTAMPTZ DEFAULT now(),
                               user_id        UUID NOT NULL,
                               last_active_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_users_user_statuses
                                   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- read_statuses
CREATE TABLE read_statuses (
                               id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                               updated_at    TIMESTAMPTZ DEFAULT now(),
                               user_id       UUID NOT NULL,
                               channel_id    UUID NOT NULL,
                               last_read_at  TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_channels_read_statuses
                                   FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
                               CONSTRAINT fk_users_read_statuses
                                   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                               CONSTRAINT uk_user_id_channel_id UNIQUE (user_id, channel_id)
);

-- message_attachments
CREATE TABLE message_attachments (
                                     message_id    UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     CONSTRAINT fk_messages_attachments
                                         FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
                                     CONSTRAINT fk_binary_contents_attachments
                                         FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE,
                                     CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id)
);
