DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS user_statues CASCADE;
DROP TABLE IF EXISTS read_statues CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;


CREATE TABLE IF NOT EXISTS binary_contents
(
    id             uuid PRIMARY KEY,
    created_at     timestamptz NOT NULL DEFAULT NOW(),
    file_name      VARCHAR(255) NOT NULL ,
    size           BIGINT NOT NULL,
    content_type   VARCHAR(255) NOT NULL,
    bytes          bytea NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id              uuid PRIMARY KEY,
    created_at      timestamptz NOT NULL DEFAULT NOW(),
    updated_at      timestamptz NOT NULL,
    username        VARCHAR(50) NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(60) NOT NULL,
    profile_id      uuid,
    CONSTRAINT fk_profile_binary FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statues
(
    id              uuid PRIMARY KEY,
    created_at      timestamptz NOT NULL DEFAULT NOW(),
    updated_at      timestamptz,
    user_id         uuid NOT NULL ,
    last_active_at  timestamptz NOT NULL,
    CONSTRAINT fk_us_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels
(
    id              uuid PRIMARY KEY,
    created_at      timestamptz NOT NULL DEFAULT NOW(),
    updated_at      timestamptz,
    name            VARCHAR(100),
    description     VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK (type in ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS read_statues
(
    id              uuid PRIMARY KEY,
    created_at      timestamptz NOT NULL DEFAULT NOW(),
    updated_at      timestamptz,
    user_id         uuid,
    channel_id      uuid,
    last_read_at    timestamptz NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id              uuid PRIMARY KEY,
    created_at      timestamptz NOT NULL DEFAULT NOW(),
    updated_at      timestamptz,
    text            TEXT,
    channel_id      uuid NOT NULL ,
    author_id       uuid NOT NULL ,
    CONSTRAINT fk_msg_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id       uuid,
    attachment_id    uuid,
    CONSTRAINT fk_atc_msg FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_atc_binary FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);


