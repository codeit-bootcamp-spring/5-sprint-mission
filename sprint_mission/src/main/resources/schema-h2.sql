-- DROP TABLE (H2용)
-- DROP TABLE IF EXISTS message_attachments;
-- DROP TABLE IF EXISTS read_statuses;
-- DROP TABLE IF EXISTS messages;
-- DROP TABLE IF EXISTS channels;
-- DROP TABLE IF EXISTS user_statuses;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS binary_contents;

-- BinaryContent
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL
    -- bytes BLOB NOT NULL  -- 필요시 주석 해제
);

-- User
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    username   VARCHAR(50) UNIQUE NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60) NOT NULL,
    profile_id UUID
);

-- UserStatus
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,
    user_id        UUID UNIQUE NOT NULL,
    last_active_at TIMESTAMP NOT NULL
);

-- Channel
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

-- Message
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    content    TEXT,
    channel_id UUID NOT NULL,
    author_id  UUID
);

-- Message.attachments
CREATE TABLE message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id)
);

-- ReadStatus
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    user_id      UUID NOT NULL,
    channel_id   UUID NOT NULL,
    last_read_at TIMESTAMP NOT NULL,
    UNIQUE (user_id, channel_id)
);

-- 제약 조건 (FK)
ALTER TABLE users
    ADD CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL;

ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_user
        FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_binary_content
        FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE;
