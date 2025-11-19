DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;

CREATE TABLE IF NOT EXISTS binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp WITH TIME ZONE NOT NULL,
    file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id         uuid PRIMARY KEY,
    created_at timestamp WITH TIME ZONE NOT NULL,
    updated_at timestamp WITH TIME ZONE,
    username   varchar(50)              NOT NULL UNIQUE,
    email      varchar(100)             NOT NULL UNIQUE,
    password   varchar(60)              NOT NULL,
    profile_id uuid
);

CREATE INDEX IF NOT EXISTS idx_users_profile ON users (profile_id);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamp WITH TIME ZONE NOT NULL,
    updated_at     timestamp WITH TIME ZONE,
    user_id        uuid                     NOT NULL UNIQUE,
    last_active_at timestamptz              NOT NULL
);

CREATE TABLE IF NOT EXISTS channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamp WITH TIME ZONE NOT NULL,
    updated_at  timestamp WITH TIME ZONE,
    type        varchar(10)              NOT NULL,
    name        varchar(100),
    description varchar(500)
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamp WITH TIME ZONE NOT NULL,
    updated_at   timestamp WITH TIME ZONE,
    user_id      uuid                     NOT NULL,
    channel_id   uuid                     NOT NULL,
    last_read_at timestamptz              NOT NULL,
    CONSTRAINT uq_read_statuses UNIQUE (user_id, channel_id)
);

CREATE INDEX IF NOT EXISTS idx_read_statuses_channel ON read_statuses (channel_id);

CREATE TABLE IF NOT EXISTS messages
(
    id         uuid PRIMARY KEY,
    created_at timestamp WITH TIME ZONE NOT NULL,
    updated_at timestamp WITH TIME ZONE,
    content    text,
    channel_id uuid                     NOT NULL,
    author_id  uuid
);

CREATE INDEX IF NOT EXISTS idx_messages_author ON messages (author_id);

CREATE INDEX IF NOT EXISTS idx_messages_channel_created ON messages (channel_id, created_at DESC);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    order_index   int  NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT uq_msg_attachments_message_order UNIQUE (message_id, order_index)
);

CREATE INDEX IF NOT EXISTS idx_msg_att_attachment ON message_attachments (attachment_id);
