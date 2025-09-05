-- DROP DATABASE IF EXISTS discodeit;
-- CREATE DATABASE discodeit ENCODING  = 'UTF8';

DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS users_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    bytes        BYTEA                    NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    user_id        UUID                     NOT NULL UNIQUE,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_userstatus_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TYPE IF EXISTS channel_type CASCADE;
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    name        VARCHAR(100),
    description VARCHAR(500),
    type        channel_type                      DEFAULT 'PUBLIC' NOT NULL
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    user_id      UUID,
    channel_id   UUID,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (user_id, channel_id),
    CONSTRAINT fk_readstatus_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_readstatus_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    content    TEXT,
    channel_id UUID                     NOT NULL,
    author_id  UUID                     NOT NULL,
    CONSTRAINT fk_messages_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    CONSTRAINT fk_msgattachments_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_msgattachments_attachment FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);