-- binary_contents
CREATE TABLE binary_contents
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes bytea NOT NULL
);

-- users
CREATE TABLE users
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    profile_id uuid,
    CONSTRAINT fk_users_profile_id FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

-- user_statuses
CREATE TABLE user_statuses
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL,
    CONSTRAINT fk_user_statuses_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- channels
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TABLE channels
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    name VARCHAR(100),
    description VARCHAR(500),
    type channel_type NOT NULL
);

-- messages
CREATE TABLE messages
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content TEXT,
    channel_id uuid NOT NULL,
    author_id uuid,
    CONSTRAINT fk_messages_channel_id FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_messages_author_id FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- message_attachments
CREATE TABLE message_attachments
(
    message_id uuid,
    attachment_id uuid,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachments_message_id FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_message_attachments_attachment_id FOREIGN KEY (attachment_id)
        REFERENCES binary_contents(id)
        ON DELETE CASCADE
);

-- read_statuses
CREATE TABLE read_statuses
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid,
    channel_id uuid,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT fk_read_statuses_user_id FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel_id FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,
    UNIQUE (user_id, channel_id)
);