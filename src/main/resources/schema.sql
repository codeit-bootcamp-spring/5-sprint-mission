CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at timestamptz  NOT NULL,
    updated_at timestamptz,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID         REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        UUID        NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    last_active_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS channel_participants
(
    channel_id UUID NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (channel_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_channel_participants_user ON channel_participants (user_id);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    TEXT,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID        REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_channel_created ON messages (channel_id, created_at DESC);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    order_index   INT  NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT uq_message_attachment_order UNIQUE (message_id, order_index)
);

CREATE INDEX IF NOT EXISTS idx_msg_attachments_message_order ON message_attachments (message_id, order_index);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT uq_read_status UNIQUE (user_id, channel_id)
);
