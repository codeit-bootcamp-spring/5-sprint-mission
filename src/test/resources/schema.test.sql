-- H2 테스트용 스크립트

DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS binary_contents;

-- 1. binary_contents
CREATE TABLE binary_contents (
                                 id VARCHAR(36) PRIMARY KEY,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(100) NOT NULL
);

-- 2. users
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
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