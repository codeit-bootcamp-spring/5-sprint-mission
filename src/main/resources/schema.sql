CREATE TABLE binary_contents (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 created_at TIMESTAMPTZ NOT NULL,
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(100) NOT NULL,
                                 bytes BYTEA NOT NULL
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       profile_id UUID,
                       CONSTRAINT fk_profile FOREIGN KEY(profile_id)
                           REFERENCES binary_contents(id)
                           ON DELETE SET NULL
);

CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               last_active_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_user FOREIGN KEY(user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT unique_user UNIQUE(user_id)
);

CREATE TABLE channels (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC','PRIVATE'))
);

CREATE TABLE messages (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_channel FOREIGN KEY(channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,
                          CONSTRAINT fk_author FOREIGN KEY(author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_user FOREIGN KEY(user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT fk_channel FOREIGN KEY(channel_id)
                                   REFERENCES channels(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT unique_user_channel UNIQUE(user_id, channel_id)
);

CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     CONSTRAINT fk_message FOREIGN KEY(message_id)
                                         REFERENCES messages(id)
                                         ON DELETE CASCADE,
                                     CONSTRAINT fk_attachment FOREIGN KEY(attachment_id)
                                         REFERENCES binary_contents(id)
                                         ON DELETE CASCADE,
                                     PRIMARY KEY(message_id, attachment_id)
);