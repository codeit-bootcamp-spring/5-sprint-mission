CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL,
    bytes        bytea        NOT NULL
);

CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   varchar(50) UNIQUE  NOT NULL,
    email      varchar(100) UNIQUE NOT NULL,
    password   varchar(60)         NOT NULL,
    profile_id uuid,
    CONSTRAINT fk_user_binary FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) NOT NULL

);

CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL,
    CONSTRAINT fk_user_statuses FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      uuid        NOT NULL,
    channel_id   uuid        NOT NULL,
    last_read_at timestamptz NOT NULL,
    UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE
);
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    text,
    channel_id uuid        NOT NULL,
    author_id  uuid,
    CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_user FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE message_attachments
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachments_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_attachments_binary FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

SELECT *
FROM user;
