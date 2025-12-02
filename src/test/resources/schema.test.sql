-- H2 테스트용 스크립트

DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS binary_contents;
DROP TABLE IF EXISTS notifications;

-- 1. binary_contents
CREATE TABLE binary_contents (
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
                       id VARCHAR(36) PRIMARY KEY,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       role varchar(20) NOT NULL DEFAULT 'ROLE_USER',
                       profile_id VARCHAR(36),
                       CONSTRAINT fk_users_profile FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id)
                           ON DELETE SET NULL
);

-- 4. channels
CREATE TABLE channels (
                          id VARCHAR(36) PRIMARY KEY,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type VARCHAR(10) NOT NULL
);

-- 5. messages
CREATE TABLE messages (
                          id VARCHAR(36) PRIMARY KEY,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          content TEXT,
                          channel_id VARCHAR(36) NOT NULL,
                          author_id VARCHAR(36),
                          CONSTRAINT fk_message_channel FOREIGN KEY (channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,
                          CONSTRAINT fk_message_author FOREIGN KEY (author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

-- 6. read_statuses
CREATE TABLE read_statuses (
                               id VARCHAR(36) PRIMARY KEY,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               user_id VARCHAR(36) NOT NULL,
                               channel_id VARCHAR(36) NOT NULL,
                               last_read_at TIMESTAMP,
                               notification_enabled boolean NOT NULL,
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
                                     message_id VARCHAR(36) NOT NULL,
                                     attachment_id VARCHAR(36) NOT NULL,
                                     CONSTRAINT fk_message_attachment_message FOREIGN KEY (message_id)
                                         REFERENCES messages(id)
                                         ON DELETE CASCADE,
                                     CONSTRAINT fk_message_attachment_binary FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id)
                                         ON DELETE CASCADE,
                                     PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE notifications
(
    id          uuid PRIMARY KEY,
    receiver_id uuid                     NOT NULL,
    created_at  timestamp with time zone NOT NULL,
    content     varchar(255)             NOT NULL,
    title       varchar(100)             NOT NULL,
    type        varchar(20)              NOT NULL,
    CONSTRAINT fk_notifications_user FOREIGN KEY (receiver_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);