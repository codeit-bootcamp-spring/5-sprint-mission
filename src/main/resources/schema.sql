-- CREATE USER discodeit_user WITH PASSWORD 'discodeit1234';
--
-- -- 2. postgres 계정은 AWS RDS 환경 특성상 완전한 super user가 아니므로, discodeit_user에 대한 권한을 추가로 부여해야함.
-- GRANT discodeit_user TO postgres;
--
-- -- 3. 'discodeit' 데이터베이스 생성 (소유자는 'discodeit_user')
-- CREATE DATABASE discodeit OWNER discodeit_user;

DROP TABLE IF EXISTS "read_statuses" CASCADE;

DROP TABLE IF EXISTS "read_status" CASCADE;

CREATE TABLE "read_status"
(
    "id"           uuid                     NOT NULL PRIMARY KEY,
    "created_at"   timestamp with time zone NOT NULL,
    "updated_at"   timestamp with time zone NULL,
    "last_read_at" timestamp with time zone NOT NULL,
    "channel_id"   uuid                     NOT NULL,
    "user_id"      uuid                     NOT NULL
);

DROP TABLE IF EXISTS "user_status" CASCADE;

CREATE TABLE "user_status"
(
    "id"             uuid                     NOT NULL PRIMARY KEY,
    "created_at"     timestamp with time zone NOT NULL,
    "updated_at"     timestamp with time zone NULL,
    "last_active_at" timestamp with time zone NOT NULL,
    "user_id"        uuid                     NOT NULL
);

DROP TABLE IF EXISTS "binary_content" CASCADE;

CREATE TABLE "binary_content"
(
    "id"           uuid                     NOT NULL PRIMARY KEY,
    "created_at"   timestamp with time zone NOT NULL,
    "file_name"    varchar(255)             NOT NULL,
    "size"         bigint                   NOT NULL,
    "content_type" varchar(100)             NOT NULL
--     "bytes"        bytea        NOT NULL
);

DROP TABLE IF EXISTS "channel" CASCADE;

CREATE TABLE "channel"
(
    "id"          uuid                     NOT NULL PRIMARY KEY,
    "created_at"  timestamp with time zone NOT NULL,
    "updated_at"  timestamp with time zone NULL,
    "name"        varchar(100)             NULL,
    "description" varchar(500)             NULL,
    "type"        varchar(10)              NOT NULL
);

DROP TABLE IF EXISTS "user" CASCADE;

CREATE TABLE "user"
(
    "id"         uuid                     NOT NULL PRIMARY KEY,
    "created_at" timestamp with time zone NOT NULL,
    "updated_at" timestamp with time zone,
    "username"   varchar(50)              NOT NULL UNIQUE,
    "email"      varchar(50)              NOT NULL UNIQUE,
    "password"   varchar(60)              NOT NULL,
    "profile_id" uuid
);

COMMENT ON COLUMN "user"."username" IS 'unique';

COMMENT ON COLUMN "user"."email" IS 'unique';

DROP TABLE IF EXISTS "message" CASCADE;

CREATE TABLE "message"
(
    "id"         uuid                     NOT NULL PRIMARY KEY,
    "created_at" timestamp with time zone NOT NULL,
    "updated_at" timestamp with time zone NULL,
    "content"    text                     NULL,
    "author_id"  uuid                     NULL,
    "channel_id" uuid                     NOT NULL
);

DROP TABLE IF EXISTS "message_attachment" CASCADE;

CREATE TABLE "message_attachment"
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
-- ALTER TABLE "channel"
--     ADD CONSTRAINT "PK_CHANNELS" PRIMARY KEY ("id");
--
-- ALTER TABLE "user"
--     ADD CONSTRAINT "PK_USERS" PRIMARY KEY ("id");
--
-- ALTER TABLE "message"
--     ADD CONSTRAINT "PK_MESSAGES" PRIMARY KEY ("id");
--
-- ALTER TABLE "message_attachments"
--     ADD CONSTRAINT "PK_MESSAGE_ATTACHMENTS" PRIMARY KEY ("attachment_id",
--                                                          "message_id");

ALTER TABLE "read_status"
    ADD CONSTRAINT "FK_channels_TO_read_statuses_1" FOREIGN KEY ("channel_id")
        REFERENCES "channel" ("id")
        ON DELETE CASCADE;

ALTER TABLE "read_status"
    ADD CONSTRAINT "FK_users_TO_read_status_1" FOREIGN KEY ("user_id")
        REFERENCES "user" ("id")
        ON DELETE CASCADE;

ALTER TABLE "user_status"
    ADD CONSTRAINT "FK_user_TO_user_status_1" FOREIGN KEY ("user_id")
        REFERENCES "user" ("id")
        ON DELETE CASCADE;

ALTER TABLE "user"
    ADD CONSTRAINT "FK_binary_content_TO_user_1" FOREIGN KEY ("profile_id")
        REFERENCES "binary_content" ("id")
        ON DELETE SET NULL
;

ALTER TABLE "message"
    ADD CONSTRAINT "FK_user_TO_message_1" FOREIGN KEY ("author_id")
        REFERENCES "user" ("id")
        ON DELETE SET NULL
;

ALTER TABLE "message"
    ADD CONSTRAINT "FK_channel_TO_message_1" FOREIGN KEY ("channel_id")
        REFERENCES "channel" ("id")
        ON DELETE CASCADE;

ALTER TABLE "message_attachment"
    ADD CONSTRAINT "FK_binary_content_TO_message_attachment_1" FOREIGN KEY ("attachment_id")
        REFERENCES "binary_content" ("id")
        ON DELETE CASCADE;

ALTER TABLE "message_attachment"
    ADD CONSTRAINT "FK_message_TO_message_attachment_1" FOREIGN KEY ("message_id")
        REFERENCES "message" ("id")
        ON DELETE CASCADE;