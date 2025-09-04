DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TYPE channelType;


create table binary_contents
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    file_name varchar(255) NOT NULL,
    size bigint not null ,
    content_type varchar(100) not null,
    bytes bytea not null
);


CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(200)             NOT NULL,
    profile_id  UUID,
    FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL

);

CREATE TYPE channelType AS ENUM ('PUBLIC', 'PRIVATE');
CREATE TABLE channels
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    name       VARCHAR(100)             not null,
    description varchar(500),
    type channelType not null
);

create table messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    content text,
    channel_id uuid NOT NULL ,
    author_id uuid,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);



create table user_statuses
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    user_id uuid not null,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

create table read_statuses
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE          DEFAULT now(),
    user_id uuid UNIQUE,
    channel_id uuid UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    UNIQUE(user_id, channel_id)
);

create table message_attachments
(
    message_id uuid,
    attachment_id uuid,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);
