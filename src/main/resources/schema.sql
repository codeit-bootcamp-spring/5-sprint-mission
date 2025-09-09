CREATE TABLE IF NOT EXISTS binary_contents (
    id           uuid PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint NOT NULL,
    content_type varchar(100) NOT NULL,
    bytes        bytea NOT NULL
);


CREATE TABLE IF NOT EXISTS users (
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    username    varchar(50)  NOT NULL UNIQUE,
    email       varchar(100) NOT NULL UNIQUE,
    password    varchar(60)  NOT NULL,
    profile_id  uuid,
    FOREIGN KEY (profile_id) REFERENCES binary_contents(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS channels (
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        varchar(100) NOT NULL,
    description varchar(500),
    type        varchar(10)  NOT NULL,
    CHECK (type IN ('PUBLIC','PRIVATE'))
);

CREATE TABLE IF NOT EXISTS messages (
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    content     text,
    channel_id  uuid NOT NULL,
    author_id   uuid,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id)  REFERENCES users(id)    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS read_statuses (
    id           uuid PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      uuid NOT NULL,
    channel_id   uuid NOT NULL,
    last_read_at timestamptz NOT NULL,
    UNIQUE (user_id, channel_id),
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_statuses (
    id            uuid PRIMARY KEY,
    created_at    timestamptz NOT NULL,
    updated_at    timestamptz,
    user_id       uuid NOT NULL UNIQUE,
    last_seen_at  timestamptz NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_attachments (
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    FOREIGN KEY (message_id)    REFERENCES messages(id)         ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id)  ON DELETE CASCADE
);