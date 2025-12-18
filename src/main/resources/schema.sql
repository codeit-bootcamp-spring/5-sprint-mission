-- 1) binary_contents
CREATE TABLE binary_contents (
                                 id           uuid PRIMARY KEY,
                                 created_at   timestamptz NOT NULL,
                                 updated_at   timestamptz,
                                 file_name    varchar(255) NOT NULL,
                                 content_type varchar(100) NOT NULL,
                                 size         bigint NOT NULL,
                                 status       varchar(20) NOT NULL DEFAULT 'PROCESSING'
);

-- 2) users
CREATE TABLE users (
                       id               uuid PRIMARY KEY,
                       created_at        timestamptz NOT NULL,
                       updated_at        timestamptz,
                       username          varchar(50)  NOT NULL,
                       password          varchar(100) NOT NULL,
                       email             varchar(100) NOT NULL,
                       default_nickname  varchar(100) NOT NULL,
                       role              varchar(20)  NOT NULL DEFAULT 'USER',
                       profile_id        uuid UNIQUE,

                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT fk_users_profile
                           FOREIGN KEY (profile_id) REFERENCES binary_contents(id)
                               ON DELETE SET NULL ON UPDATE CASCADE
);

-- 3) channels
CREATE TABLE channels (
                          id          uuid PRIMARY KEY,
                          created_at  timestamptz NOT NULL,
                          updated_at  timestamptz,
                          type        varchar(10)  NOT NULL,
                          name        varchar(100) NOT NULL,
                          description varchar(1000),

                          CONSTRAINT uk_channels_name UNIQUE (name),
                          CONSTRAINT check_channels_type CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

-- 4) messages
CREATE TABLE messages (
                          id         uuid PRIMARY KEY,
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          content    text,
                          author_id  uuid,
                          channel_id uuid NOT NULL,

                          CONSTRAINT fk_messages_author
                              FOREIGN KEY (author_id) REFERENCES users(id)
                                  ON DELETE SET NULL ON UPDATE CASCADE,

                          CONSTRAINT fk_messages_channel
                              FOREIGN KEY (channel_id) REFERENCES channels(id)
                                  ON DELETE CASCADE ON UPDATE CASCADE
);

-- 5) message_attachments (join table)
CREATE TABLE message_attachments (
                                     message_id    uuid NOT NULL,
                                     attachment_id uuid NOT NULL,

                                     PRIMARY KEY (message_id, attachment_id),

                                     CONSTRAINT fk_ma_message
                                         FOREIGN KEY (message_id) REFERENCES messages(id)
                                             ON DELETE CASCADE ON UPDATE CASCADE,

                                     CONSTRAINT fk_ma_attachment
                                         FOREIGN KEY (attachment_id) REFERENCES binary_contents(id)
                                             ON DELETE CASCADE ON UPDATE CASCADE
);

-- 6) read_statuses
CREATE TABLE read_statuses (
                               id                    uuid PRIMARY KEY,
                               created_at            timestamptz NOT NULL,
                               updated_at            timestamptz,
                               last_read_at          timestamptz NOT NULL,
                               user_id               uuid NOT NULL,
                               channel_id            uuid NOT NULL,
                               notification_enabled  boolean NOT NULL DEFAULT false,

                               CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id),

                               CONSTRAINT fk_rs_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE CASCADE ON UPDATE CASCADE,

                               CONSTRAINT fk_rs_channel
                                   FOREIGN KEY (channel_id) REFERENCES channels(id)
                                       ON DELETE CASCADE ON UPDATE CASCADE
);

-- 7) notifications
CREATE TABLE notifications (
                               id          uuid PRIMARY KEY,
                               created_at  timestamptz NOT NULL,
                               receiver_id uuid NOT NULL,
                               title       varchar(255) NOT NULL,
                               content     text NOT NULL,

                               CONSTRAINT fk_notifications_receiver
                                   FOREIGN KEY (receiver_id) REFERENCES users(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_notifications_receiver_id ON notifications (receiver_id);
CREATE INDEX idx_notifications_created_at ON notifications (created_at DESC);

-- 8) user_statuses  (엔티티 코드가 없어서 너가 쓰던 정의 유지)
CREATE TABLE user_statuses (
                               id             uuid PRIMARY KEY,
                               created_at     timestamptz NOT NULL,
                               updated_at     timestamptz,
                               last_active_at timestamptz NOT NULL,
                               user_id        uuid NOT NULL,

                               CONSTRAINT uk_user_statuses_user UNIQUE (user_id),

                               CONSTRAINT fk_us_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE CASCADE ON UPDATE CASCADE
);
