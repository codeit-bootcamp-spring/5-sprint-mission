CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TYPE IF EXISTS type CASCADE ;
CREATE TYPE type AS ENUM('PUBLIC','PRIVATE');

DROP TABLE IF EXISTS channels;
CREATE TABLE channels(
                         id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                         created_at timestamptz NOT NULL,
                         updated_at timestamptz,
                         name VARCHAR(100),
                         description VARCHAR(500),
                         type VARCHAR(10) NOT NULL
);

DROP TABLE IF EXISTS binary_contents;
CREATE TABLE binary_contents(
                                id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                created_at timestamptz NOT NULL,
                                file_name VARCHAR(255) NOT NULL,
                                size BIGINT NOT NULL,
                                content_type VARCHAR(100) NOT NULL,
                                bytes bytea NOT NULL
);

DROP TABLE IF EXISTS users;
CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamptz NOT NULL ,
    updated_at timestamptz,
    username VARCHAR(50) NOT NULL UNIQUE ,
    email VARCHAR(100) NOT NULL UNIQUE ,
    password VARCHAR(60) NOT NULL ,
    profile_id uuid, -- 외래키
    FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS user_statuses;
CREATE TABLE user_statuses(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid UNIQUE NOT NULL , -- 외래키
    foreign key (user_id) references users(id) ON DELETE CASCADE,
    last_active_at timestamptz NOT NULL
);

DROP TABLE IF EXISTS read_statuses;
CREATE TABLE read_statuses(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid, -- 외래키
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    channel_id uuid, --외래키
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    last_read_at timestamptz NOT NULL,
    UNIQUE (user_id, channel_id)
);

DROP TABLE IF EXISTS messages;
CREATE TABLE messages(
                         id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                         created_at timestamptz NOT NULL,
                         updated_at timestamptz,
                         content TEXT,
                         channel_id UUID NOT NULL,
                        FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
                         author_id uuid,
                        FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS message_attachments;
CREATE TABLE message_attachments(
    message_id uuid,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    attachment_id uuid,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);

