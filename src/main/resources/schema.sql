DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 바이너리 콘텐츠
CREATE TABLE binary_contents (
                                   id UUID PRIMARY KEY,
                                   created_at TIMESTAMPTZ NOT NULL,
                                   file_name VARCHAR(255) NOT NULL,
                                   size BIGINT NOT NULL,
                                   content_type VARCHAR(100) NOT NULL,
                                   bytes BYTEA NOT NULL
  );

-- 유저
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(60) NOT NULL,
                       profile_id UUID,
                       CONSTRAINT fk_users_profile
                           FOREIGN KEY (profile_id)
                               REFERENCES binary_contents(id)
                               ON DELETE SET NULL
);

-- 채널
CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

-- 메시지
CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ NOT NULL,
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_messages_channel
                              FOREIGN KEY (channel_id)
                                  REFERENCES channels(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_messages_author
                              FOREIGN KEY (author_id)
                                  REFERENCES users(id)
                                  ON DELETE SET NULL
);

-- 읽음 상태
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ NOT NULL,
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_read_statuses_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_read_statuses_channel
                                   FOREIGN KEY (channel_id)
                                       REFERENCES channels(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT uk_read_statuses UNIQUE (user_id, channel_id)
);

-- 유저 상태
CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ NOT NULL,
                               user_id UUID NOT NULL,
                               last_active_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_user_statuses_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT uk_user_statuses UNIQUE (user_id)
);

-- 메시지 첨부 파일 매핑
CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     CONSTRAINT fk_message_attachments_message
                                         FOREIGN KEY (message_id)
                                             REFERENCES messages(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_message_attachments_attachment
                                         FOREIGN KEY (attachment_id)
                                             REFERENCES binary_contents(id)
                                             ON DELETE CASCADE,
                                     CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id)
);

select * from users;
select * from channels;
select * from read_statuses;
select * from messages