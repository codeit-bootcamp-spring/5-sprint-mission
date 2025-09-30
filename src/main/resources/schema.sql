-- DROP DATABASE IF EXISTS discodeit;
-- CREATE DATABASE discodeit ENCODING = 'UTF8';
--
-- CREATE USER discodeit_user WITH ENCRYPTED PASSWORD 'discodeit1234';
-- GRANT ALL PRIVILEGES ON DATABASE discodeit TO discodeit_user;
--
-- DROP TABLE IF EXISTS binary_contents CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS user_statuses CASCADE;
-- DROP TABLE IF EXISTS channels CASCADE;
-- DROP TABLE IF EXISTS messages CASCADE;
-- DROP TABLE IF EXISTS read_statuses CASCADE;
-- DROP TABLE IF EXISTS message_attachments CASCADE;
--
-- -- 1. binary_contents
-- CREATE TABLE binary_contents (
--                                  id UUID PRIMARY KEY,
--                                  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                                  file_name VARCHAR(255) NOT NULL,
--                                  size BIGINT NOT NULL,
--                                  content_type VARCHAR(100) NOT NULL
-- );
--
-- -- 2. users
-- CREATE TABLE users (
--                        id UUID PRIMARY KEY,
--                        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                        updated_at TIMESTAMPTZ DEFAULT NOW(),
--                        username VARCHAR(50) NOT NULL UNIQUE,
--                        email VARCHAR(100) NOT NULL UNIQUE,
--                        password VARCHAR(60) NOT NULL,
--                        profile_id UUID,
--                        CONSTRAINT fk_users_profile FOREIGN KEY (profile_id)
--                            REFERENCES binary_contents(id)
--                            ON DELETE SET NULL
-- );
--
-- -- 3. user_statuses
-- CREATE TABLE user_statuses (
--                                id UUID PRIMARY KEY,
--                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                                updated_at TIMESTAMPTZ DEFAULT NOW(),
--                                user_id UUID NOT NULL UNIQUE,
--                                last_active_at TIMESTAMPTZ NOT NULL,
--                                CONSTRAINT fk_user_status_user FOREIGN KEY (user_id)
--                                    REFERENCES users(id)
--                                    ON DELETE CASCADE
-- );
--
-- -- 4. channels
-- CREATE TABLE channels (
--                           id UUID PRIMARY KEY,
--                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                           updated_at TIMESTAMPTZ DEFAULT NOW(),
--                           name VARCHAR(100),
--                           description VARCHAR(500),
--                           type VARCHAR(10) NOT NULL  -- enum 대신 문자열로 변경
-- );
--
--
-- -- 5. messages
-- CREATE TABLE messages (
--                           id UUID PRIMARY KEY,
--                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                           updated_at TIMESTAMPTZ DEFAULT NOW(),
--                           content TEXT,
--                           channel_id UUID NOT NULL,
--                           author_id UUID,
--                           CONSTRAINT fk_message_channel FOREIGN KEY (channel_id)
--                               REFERENCES channels(id)
--                               ON DELETE CASCADE,
--                           CONSTRAINT fk_message_author FOREIGN KEY (author_id)
--                               REFERENCES users(id)
--                               ON DELETE SET NULL
-- );
--
-- -- 6. read_statuses
-- CREATE TABLE read_statuses (
--                                id UUID PRIMARY KEY,
--                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
--                                updated_at TIMESTAMPTZ DEFAULT NOW(),
--                                user_id UUID NOT NULL,
--                                channel_id UUID NOT NULL,
--                                last_read_at TIMESTAMPTZ,
--                                CONSTRAINT fk_read_status_user FOREIGN KEY (user_id)
--                                    REFERENCES users(id)
--                                    ON DELETE CASCADE,
--                                CONSTRAINT fk_read_status_channel FOREIGN KEY (channel_id)
--                                    REFERENCES channels(id)
--                                    ON DELETE CASCADE,
--                                CONSTRAINT uq_read_status UNIQUE (user_id, channel_id)
-- );
--
-- -- 7. message_attachments
-- CREATE TABLE message_attachments (
--                                      message_id UUID NOT NULL,
--                                      attachment_id UUID NOT NULL,
--                                      CONSTRAINT fk_message_attachment_message FOREIGN KEY (message_id)
--                                          REFERENCES messages(id)
--                                          ON DELETE CASCADE,
--                                      CONSTRAINT fk_message_attachment_binary FOREIGN KEY (attachment_id)
--                                          REFERENCES binary_contents(id)
--                                          ON DELETE CASCADE,
--                                      PRIMARY KEY (message_id, attachment_id)
-- );
--
-- ALTER TABLE binary_contents OWNER TO discodeit_user;
-- ALTER TABLE users OWNER TO discodeit_user;
-- ALTER TABLE user_statuses OWNER TO discodeit_user;
-- ALTER TABLE channels OWNER TO discodeit_user;
-- ALTER TABLE messages OWNER TO discodeit_user;
-- ALTER TABLE read_statuses OWNER TO discodeit_user;
-- ALTER TABLE message_attachments OWNER TO discodeit_user;


-- H2 테스트용 스크립트

DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS user_statuses;
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

-- 3. user_statuses
CREATE TABLE user_statuses (
                               id VARCHAR(36) PRIMARY KEY,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               user_id VARCHAR(36) NOT NULL UNIQUE,
                               last_active_at TIMESTAMP NOT NULL,
                               CONSTRAINT fk_user_status_user FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE
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
