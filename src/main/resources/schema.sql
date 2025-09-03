DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TYPE IF EXISTS channel_type CASCADE;

CREATE TABLE "users"
(
    "id"         uuid         NOT NULL,
    "profile_id" uuid         NULL,
    "created_at" timestamptz  NOT NULL,
    "updated_at" timestamptz  NULL,
    "username"   VARCHAR(50)  NOT NULL UNIQUE,
    "email"      VARCHAR(100) NOT NULL UNIQUE,
    "password"   VARCHAR(60)  NOT NULL
);

CREATE TABLE "channels"
(
    "id"          uuid         NOT NULL,
    "created_at"  timestamptz  NOT NULL,
    "updated_at"  timestamptz  NULL,
    "name"        VARCHAR(100) NULL,
    "description" VARCHAR(500) NULL,
    "type"        VARCHAR(10)  NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE "messages"
(
    "id"         uuid        NOT NULL,
    "channel_id" uuid        NOT NULL,
    "author_id"  uuid        NULL,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz NULL,
    "content"    TEXT        NULL
);

CREATE TABLE "binary_contents"
(
    "id"           uuid         NOT NULL,
    "created_at"   timestamptz  NOT NULL,
    "file_name"    VARCHAR(255) NOT NULL,
    "size"         BIGINT       NOT NULL,
    "content_type" VARCHAR(100) NOT NULL
);

CREATE TABLE "message_attachments"
(
    "message_id"    uuid NULL,
    "attachment_id" uuid NULL
);

CREATE TABLE "user_statuses"
(
    "id"             uuid        NOT NULL,
    "user_id"        uuid        NOT NULL UNIQUE,
    "created_at"     timestamptz NOT NULL,
    "updated_at"     timestamptz NULL,
    "last_active_at" timestamptz NOT NULL
);

CREATE TABLE "read_statuses"
(
    "id"           uuid        NOT NULL,
    "user_id"      uuid        NOT NULL,
    "channel_id"   uuid        NOT NULL,
    "created_at"   timestamptz NOT NULL,
    "updated_at"   timestamptz NULL,
    "last_read_at" timestamptz NOT NULL,
    UNIQUE ("user_id", "channel_id")
);

ALTER TABLE "users"
    ADD CONSTRAINT "PK_USERS" PRIMARY KEY ("id");

ALTER TABLE "channels"
    ADD CONSTRAINT "PK_CHANNELS" PRIMARY KEY ("id");

ALTER TABLE "messages"
    ADD CONSTRAINT "PK_MESSAGES" PRIMARY KEY ("id");

ALTER TABLE "binary_contents"
    ADD CONSTRAINT "PK_BINARY_CONTENTS" PRIMARY KEY ("id");

ALTER TABLE "user_statuses"
    ADD CONSTRAINT "PK_USER_STATUSES" PRIMARY KEY ("id");

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "PK_READ_STATUSES" PRIMARY KEY ("id");

ALTER TABLE "users"
    ADD CONSTRAINT "FK_binary_contents_TO_users_1" FOREIGN KEY ("profile_id")
        REFERENCES "binary_contents" ("id") ON DELETE SET NULL;

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_channels_TO_messages_1" FOREIGN KEY ("channel_id")
        REFERENCES "channels" ("id") ON DELETE CASCADE;

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_users_TO_messages_1" FOREIGN KEY ("author_id")
        REFERENCES "users" ("id") ON DELETE SET NULL;

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_messages_TO_message_attachments_1" FOREIGN KEY ("message_id")
        REFERENCES "messages" ("id") ON DELETE CASCADE;

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_binary_contents_TO_message_attachments_1" FOREIGN KEY ("attachment_id")
        REFERENCES "binary_contents" ("id") ON DELETE CASCADE;

ALTER TABLE "user_statuses"
    ADD CONSTRAINT "FK_users_TO_user_statuses_1" FOREIGN KEY ("user_id")
        REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_users_TO_read_statuses_1" FOREIGN KEY ("user_id")
        REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_channels_TO_read_statuses_1" FOREIGN KEY ("channel_id")
        REFERENCES "channels" ("id") ON DELETE CASCADE;

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "PK_message_attachments" PRIMARY KEY ("message_id", "attachment_id");

SELECT *
FROM channels;

SELECT *
FROM users;

SELECT *
FROM read_statuses;