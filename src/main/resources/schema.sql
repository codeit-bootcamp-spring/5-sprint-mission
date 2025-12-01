-- DROP DATABASE IF EXISTS discodeit;
-- CREATE DATABASE discodeit ENCODING = 'UTF8';
--
-- CREATE USER discodeit_user WITH ENCRYPTED PASSWORD 'discodeit1234';
-- GRANT ALL PRIVILEGES ON DATABASE discodeit TO discodeit_user;

DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

-- 1. binary_contents
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL,
    status       varchar(20)              NOT NULL DEFAULT 'PROCESSING'
);

-- 2. users
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ DEFAULT NOW(),
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       role varchar(20) NOT NULL DEFAULT 'ROLE_USER',
                       profile_id UUID,
                       CONSTRAINT fk_users_profile FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id)
                           ON DELETE SET NULL
);

-- 4. channels
CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ DEFAULT NOW(),
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type VARCHAR(10) NOT NULL  -- enum 대신 문자열로 변경
);


-- 5. messages
CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ DEFAULT NOW(),
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_message_channel FOREIGN KEY (channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,
                          CONSTRAINT fk_message_author FOREIGN KEY (author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

-- 6. read_statuses
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ DEFAULT NOW(),
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ,
                               CONSTRAINT fk_read_status_user FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id)
                                   REFERENCES channels(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT uq_read_status UNIQUE (user_id, channel_id)
);

-- 7. message_attachments
CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     CONSTRAINT fk_message_attachment_message FOREIGN KEY (message_id)
                                         REFERENCES messages(id)
                                         ON DELETE CASCADE,
                                     CONSTRAINT fk_message_attachment_binary FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id)
                                         ON DELETE CASCADE,
                                     PRIMARY KEY (message_id, attachment_id)
);

ALTER TABLE binary_contents OWNER TO discodeit_user;
ALTER TABLE users OWNER TO discodeit_user;
ALTER TABLE channels OWNER TO discodeit_user;
ALTER TABLE messages OWNER TO discodeit_user;
ALTER TABLE read_statuses OWNER TO discodeit_user;
ALTER TABLE message_attachments OWNER TO discodeit_user;