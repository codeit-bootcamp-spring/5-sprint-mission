/*
username: discodeit_user
password: discodeit1234
*/


-- ✅ 1. 참조 대상 먼저 만들기 (binary_contents)
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP    NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BLOB         NOT NULL,
    message_id   UUID,
    CONSTRAINT fk_binary_contents_message
        FOREIGN KEY (message_id) REFERENCES messages (id)
            ON DELETE SET NULL
);

-- ✅ 2. users: binary_contents.profile_id 참조
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID,
    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id) REFERENCES binary_contents (id)
            ON DELETE SET NULL
);

-- ✅ 3. channels: 독립 테이블
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
    -- ENUM(PUBLIC, PRIVATE)은 나중에 ENUM 타입으로 대체 가능
);

-- ✅ 4. messages: users.id, channels.id 참조
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    content    TEXT,
    channel_id UUID      NOT NULL,
    author_id  UUID,
    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id) REFERENCES users (id)
            ON DELETE SET NULL
);

-- ✅ 5. user_statuses: users.id 참조
CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,
    user_id        UUID      NOT NULL UNIQUE,
    last_active_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_status_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

-- ✅ 6. read_statuses: users.id + channels.id 참조
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    user_id      UUID      NOT NULL,
    channel_id   UUID      NOT NULL,
    last_read_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_read_status UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id) REFERENCES channels (id)
            ON DELETE CASCADE
);

-- ✅ 7. message_attachments: messages.id + binary_contents.id 참조
CREATE TABLE message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_msg_attachment_message
        FOREIGN KEY (message_id) REFERENCES messages (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_msg_attachment_binary
        FOREIGN KEY (attachment_id) REFERENCES binary_contents (id)
            ON DELETE CASCADE
);
