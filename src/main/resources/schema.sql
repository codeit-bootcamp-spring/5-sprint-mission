DROP TABLE IF EXISTS binary_contents CASCADE;

CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ           DEFAULT NOW(),
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID, -- binary_contents_id

    CONSTRAINT users_binary_contents_id_fk FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS user_statuses CASCADE;

CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ          DEFAULT NOW(),
    user_id        UUID        NOT NULL UNIQUE, -- users_id
    last_active_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT user_statuses_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS channels CASCADE;

CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ          DEFAULT NOW(),
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

DROP TABLE IF EXISTS messages CASCADE;

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ          DEFAULT NOW(),
    content    TEXT,
    channel_id UUID        NOT NULL, -- channels_id
    author_id  UUID,                 -- users_id

    CONSTRAINT messages_channels_id_fk FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT messages_users_id_fk FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS read_statuses CASCADE;

CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ          DEFAULT NOW(),
    user_id      UUID UNIQUE, -- users_id
    channel_id   UUID UNIQUE, -- channels_id
    last_read_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT read_statuses_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT read_statuses_channels_id_fk FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS message_attachments CASCADE;

CREATE TABLE message_attachments
(
    message_id    UUID, -- messages_id
    attachment_id UUID, -- binary_contents_id

    CONSTRAINT message_attachments_messages_id_fk FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT message_attachments_binary_contents_id_fk FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

SELECT VERSION();