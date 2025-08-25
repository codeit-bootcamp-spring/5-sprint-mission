CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW(); RETURN NEW;
END
$$ LANGUAGE plpgsql;

DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'channel_type') THEN CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');
        END IF;
    END
$$;

CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at   timestamptz  NOT NULL DEFAULT NOW(),
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at timestamptz  NOT NULL DEFAULT NOW(),
    updated_at timestamptz,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID         NULL REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at     timestamptz NOT NULL DEFAULT NOW(),
    updated_at     timestamptz,
    user_id        UUID        NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    last_active_at timestamptz NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_user_statuses_updated_at
    BEFORE UPDATE
    ON user_statuses
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at  timestamptz  NOT NULL DEFAULT NOW(),
    updated_at  timestamptz,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        channel_type NOT NULL
);

CREATE TRIGGER trg_channels_updated_at
    BEFORE UPDATE
    ON channels
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS channel_participants
(
    channel_id UUID NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (channel_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_channel_participants_user ON channel_participants (user_id);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at timestamptz NOT NULL DEFAULT NOW(),
    updated_at timestamptz,
    content    TEXT,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID        REFERENCES users (id) ON DELETE SET NULL
);

CREATE TRIGGER trg_messages_updated_at
    BEFORE UPDATE
    ON messages
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_messages_channel_created ON messages (channel_id, created_at DESC);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    created_at   timestamptz NOT NULL DEFAULT NOW(),
    updated_at   timestamptz,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT uq_read_status UNIQUE (user_id, channel_id)
);

CREATE TRIGGER trg_read_statuses_updated_at
    BEFORE UPDATE
    ON read_statuses
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
