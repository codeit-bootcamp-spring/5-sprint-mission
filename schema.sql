DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

-- binary_contents 테이블
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL
);

-- users 테이블
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50) UNIQUE       NOT NULL,
    email      VARCHAR(100) UNIQUE      NOT NULL,
    password   VARCHAR(60)              NOT NULL,
    profile_id uuid,
    CONSTRAINT fk_users_binary_contents FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- channels 테이블
CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL CHECK ( type IN ('PUBLIC', 'PRIVATE'))
);

-- user_statuses 테이블
CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    user_id        uuid UNIQUE              NOT NULL,
    last_active_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_user_statuses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- messages 테이블
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT,
    channel_id uuid                     NOT NULL,
    author_id  uuid,
    CONSTRAINT fk_messages_channels FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_users FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);


-- read_statuses 테이블
CREATE TABLE read_statuses
(
    id         uuid PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    channel_id uuid                     NOT NULL,
    user_id    uuid,
    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_statuses_channels FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- message_attachments 테이블
CREATE TABLE message_attachments
(
    message_id    uuid,
    attachment_id uuid,
    CONSTRAINT fk_massage_attachments_messages FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_massage_attachments_binary_contents FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);