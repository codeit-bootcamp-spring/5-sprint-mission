DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===== 테이블 생성 (외래 키 제약 조건 없이) =====

-- 기본 테이블들 먼저 생성
CREATE TABLE binary_contents (
    id            uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at    timestamptz NOT NULL,
    file_name     varchar(255) NOT NULL,
    size          bigint NOT NULL,
    content_type  varchar(100) NOT NULL
--     bytes         bytea NOT NULL  -- 필요에 따라 제거 가능
);

CREATE TABLE users (
    id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    username     varchar(50)  UNIQUE NOT NULL,
    email        varchar(100) UNIQUE NOT NULL,
    password     varchar(60)  NOT NULL,
    profile_id   uuid
);

CREATE TABLE channels (
    id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    name         varchar(100),
    description  varchar(500),
    type         varchar(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE messages (
    id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    content      text,
    channel_id   uuid NOT NULL,
    author_id    uuid
);

CREATE TABLE user_statuses (
    id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL
);

CREATE TABLE read_statuses (
    id            uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at    timestamptz NOT NULL,
    updated_at    timestamptz,
    user_id       uuid NOT NULL,
    channel_id    uuid NOT NULL,
    last_read_at  timestamptz NOT NULL,
    UNIQUE (user_id, channel_id)
);

CREATE TABLE message_attachments (
    message_id    uuid,
    attachment_id uuid,
    PRIMARY KEY (message_id, attachment_id)
);

-- ===== 외래 키 제약 조건 추가 =====

-- User -> BinaryContent (profile)
ALTER TABLE users
    ADD CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id) REFERENCES binary_contents(id)
            ON DELETE SET NULL;

-- UserStatus -> User
ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE;

-- Message -> Channel
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id)
            ON DELETE CASCADE;

-- Message -> User (author)
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id) REFERENCES users(id)
            ON DELETE SET NULL;

-- ReadStatus -> User
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE;

-- ReadStatus -> Channel
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id) REFERENCES channels(id)
            ON DELETE CASCADE;

-- MessageAttachment -> Message
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachments_message
        FOREIGN KEY (message_id) REFERENCES messages(id)
            ON DELETE CASCADE;

-- MessageAttachment -> BinaryContent
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachments_attachment
        FOREIGN KEY (attachment_id) REFERENCES binary_contents(id)
            ON DELETE CASCADE;