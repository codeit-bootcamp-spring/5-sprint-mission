-- Todo CASCADE

DROP TABLE IF EXISTS "read_statuses" CASCADE;

CREATE TABLE "read_statuses"
(
    "id"           uuid        NOT NULL PRIMARY KEY,
    "created_at"   timestamptz NOT NULL,
    "updated_at"   timestamptz NULL,
    "last_read_at" timestamptz NOT NULL,
    "channel_id"   uuid        NOT NULL,
    "user_id"      uuid        NOT NULL
);

DROP TABLE IF EXISTS "user_statuses" CASCADE;

CREATE TABLE "user_statuses"
(
    "id"             uuid        NOT NULL PRIMARY KEY,
    "created_at"     timestamptz NOT NULL,
    "updated_at"     timestamptz NULL,
    "last_active_at" timestamptz NOT NULL,
    "user_id"        uuid        NOT NULL
);

DROP TABLE IF EXISTS "binary_contents" CASCADE;

CREATE TABLE "binary_contents"
(
    "id"           uuid         NOT NULL PRIMARY KEY,
    "created_at"   timestamptz  NOT NULL,
    "file_name"    varchar(255) NOT NULL,
    "size"         bigint       NOT NULL,
    "content_type" varchar(100) NOT NULL,
    "bytes"        bytea        NOT NULL
);

DROP TABLE IF EXISTS "channels" CASCADE;

CREATE TABLE "channels"
(
    "id"          uuid         NOT NULL PRIMARY KEY,
    "created_at"  timestamptz  NOT NULL,
    "updated_at"  timestamptz  NULL,
    "name"        varchar(100) NULL,
    "description" varchar(500) NULL,
    "type"        varchar(10)  NOT NULL
);

DROP TABLE IF EXISTS "users" CASCADE;

CREATE TABLE "users"
(
    "id"         uuid        NOT NULL PRIMARY KEY,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz NOT NULL,
    "username"   varchar(50) NOT NULL UNIQUE,
    "email"      varchar(50) NOT NULL UNIQUE,
    "password"   varchar(60) NOT NULL,
    "profile_id" uuid        NOT NULL
);

COMMENT ON COLUMN "users"."username" IS 'unique';

COMMENT ON COLUMN "users"."email" IS 'unique';

DROP TABLE IF EXISTS "messages" CASCADE;

CREATE TABLE "messages"
(
    "id"         uuid        NOT NULL PRIMARY KEY,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz NULL,
    "content"    text        NULL,
    "author_id"  uuid        NULL,
    "channel_id" uuid        NOT NULL
);

DROP TABLE IF EXISTS "message_attachments" CASCADE;

CREATE TABLE "message_attachments"
(
    "attachment_id" uuid NOT NULL,
    "message_id"    uuid NOT NULL
);

-- ALTER TABLE "read_statuses"
--     ADD CONSTRAINT "PK_READ_STATUS" PRIMARY KEY ("id");
--
-- ALTER TABLE "user_statuses"
--     ADD CONSTRAINT "PK_USER_STATUSES" PRIMARY KEY ("id");
--
-- ALTER TABLE "binary_contents"
--     ADD CONSTRAINT "PK_BINARY_CONTENTS" PRIMARY KEY ("id");
--
-- ALTER TABLE "channels"
--     ADD CONSTRAINT "PK_CHANNELS" PRIMARY KEY ("id");
--
-- ALTER TABLE "users"
--     ADD CONSTRAINT "PK_USERS" PRIMARY KEY ("id");
--
-- ALTER TABLE "messages"
--     ADD CONSTRAINT "PK_MESSAGES" PRIMARY KEY ("id");
--
-- ALTER TABLE "message_attachments"
--     ADD CONSTRAINT "PK_MESSAGE_ATTACHMENTS" PRIMARY KEY ("attachment_id",
--                                                          "message_id");

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_channels_TO_read_statuses_1" FOREIGN KEY ("channel_id")
        REFERENCES "channels" ("id");

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_users_TO_read_statuses_1" FOREIGN KEY ("user_id")
        REFERENCES "users" ("id")
        ON DELETE CASCADE;

ALTER TABLE "user_statuses"
    ADD CONSTRAINT "FK_users_TO_user_statuses_1" FOREIGN KEY ("user_id")
        REFERENCES "users" ("id")
        ON DELETE CASCADE;

ALTER TABLE "users"
    ADD CONSTRAINT "FK_binary_contents_TO_users_1" FOREIGN KEY ("profile_id")
        REFERENCES "binary_contents" ("id");

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_users_TO_messages_1" FOREIGN KEY ("author_id")
        REFERENCES "users" ("id");

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_channels_TO_messages_1" FOREIGN KEY ("channel_id")
        REFERENCES "channels" ("id")
        ON DELETE CASCADE;

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_binary_contents_TO_message_attachments_1" FOREIGN KEY ("attachment_id")
        REFERENCES "binary_contents" ("id")
        ON DELETE CASCADE;

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_messages_TO_message_attachments_1" FOREIGN KEY ("message_id")
        REFERENCES "messages" ("id")
        ON DELETE CASCADE;