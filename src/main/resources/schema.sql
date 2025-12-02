-- CREATE TABLE

CREATE TABLE "user_statuses"
(
    "id"             uuid        NOT NULL,
    "created_at"     timestamptz NOT NULL,
    "updated_at"     timestamptz NULL,
    "last_active_at" timestamptz NOT NULL,
    "user_id"        uuid        NOT NULL
);

CREATE TABLE "read_statuses"
(
    "id"           uuid        NOT NULL,
    "created_at"   timestamptz NOT NULL,
    "updated_at"   timestamptz NULL,
    "last_read_at" timestamptz NOT NULL,
    "user_id"      uuid        NULL,
    "channel_id"   uuid        NULL
);

CREATE TABLE "channels"
(
    "id"          uuid         NOT NULL,
    "created_at"  timestamptz  NOT NULL,
    "updated_at"  timestamptz  NULL,
    "name"        varchar(100) NULL,
    "description" varchar(500) NULL,
    "type"        varchar(10)  NOT NULL
);

CREATE TABLE "message_attachments"
(
    "attachment_id" uuid NOT NULL,
    "message_id"    uuid NOT NULL
);

CREATE TABLE "binary_contents"
(
    "id"           uuid         NOT NULL,
    "created_at"   timestamptz  NOT NULL,
    "file_name"    varchar(255) NOT NULL,
    "size"         bigint       NOT NULL,
    "content_type" varchar(100) NOT NULL,
    "bytes"        bytea        NOT NULL
);

CREATE TABLE "users"
(
    "id"         uuid         NOT NULL,
    "created_at" timestamptz  NOT NULL,
    "updated_at" timestamptz  NULL,
    "username"   varchar(50)  NOT NULL,
    "email"      varchar(100) NOT NULL,
    "password"   varchar(60)  NOT NULL,
    "profile_id" uuid         NULL
);

CREATE TABLE "messages"
(
    "id"         uuid        NOT NULL,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz NULL,
    "content"    text        NULL,
    "author_id"  uuid        NULL,
    "channel_id" uuid        NOT NULL
);

CREATE TABLE notifications
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    receiver_id uuid                     NOT NULL,
    title       varchar(255)             NOT NULL,
    content     text                     NOT NULL,
    FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_receiver_id ON notifications (receiver_id);
CREATE INDEX idx_notifications_created_at ON notifications (created_at DESC);

-- PK 설정

ALTER TABLE "user_statuses"
    ADD CONSTRAINT "PK_USER_STATUSES" PRIMARY KEY (
                                                   "id"
        );

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "PK_READ_STATUSES" PRIMARY KEY (
                                                   "id"
        );

ALTER TABLE "channels"
    ADD CONSTRAINT "PK_CHANNELS" PRIMARY KEY (
                                              "id"
        );

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "PK_MESSAGE_ATTACHMENTS" PRIMARY KEY (
                                                         "attachment_id",
                                                         "message_id"
        );

ALTER TABLE "binary_contents"
    ADD CONSTRAINT "PK_BINARY_CONTENTS" PRIMARY KEY (
                                                     "id"
        );

ALTER TABLE "users"
    ADD CONSTRAINT "PK_USERS" PRIMARY KEY (
                                           "id"
        );

ALTER TABLE "messages"
    ADD CONSTRAINT "PK_MESSAGES" PRIMARY KEY (
                                              "id"
        );

-- FK 설정

ALTER TABLE "user_statuses"
    ADD CONSTRAINT "FK_users_TO_user_statuses_1" FOREIGN KEY (
                                                              "user_id"
        )
        REFERENCES "users" (
                            "id"
            );

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_users_TO_read_statuses_1" FOREIGN KEY (
                                                              "user_id"
        )
        REFERENCES "users" (
                            "id"
            );

ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_channels_TO_read_statuses_1" FOREIGN KEY (
                                                                 "channel_id"
        )
        REFERENCES "channels" (
                               "id"
            );

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_binary_contents_TO_message_attachments_1" FOREIGN KEY (
                                                                              "attachment_id"
        )
        REFERENCES "binary_contents" (
                                      "id"
            );

ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_messages_TO_message_attachments_1" FOREIGN KEY (
                                                                       "message_id"
        )
        REFERENCES "messages" (
                               "id"
            );

ALTER TABLE "users"
    ADD CONSTRAINT "FK_binary_contents_TO_users_1" FOREIGN KEY (
                                                                "profile_id"
        )
        REFERENCES "binary_contents" (
                                      "id"
            );

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_users_TO_messages_1" FOREIGN KEY (
                                                         "author_id"
        )
        REFERENCES "users" (
                            "id"
            );

ALTER TABLE "messages"
    ADD CONSTRAINT "FK_channels_TO_messages_1" FOREIGN KEY (
                                                            "channel_id"
        )
        REFERENCES "channels" (
                               "id"
            );

-- UK 설정
ALTER TABLE "users"
    ADD CONSTRAINT "UK_USERS_USERNAME" UNIQUE ("username");
ALTER TABLE "users"
    ADD CONSTRAINT "UK_USERS_EMAIL" UNIQUE ("email");
ALTER TABLE "channels"
    ADD CONSTRAINT "UK_CHANNELS_NAME" UNIQUE ("name");
ALTER TABLE "user_statuses"
    ADD CONSTRAINT "UK_USER_STATUSES_USER" UNIQUE ("user_id");
ALTER TABLE "read_statuses"
    ADD CONSTRAINT "UK_READ_STATUSES_USER_CHANNEL" UNIQUE ("user_id", "channel_id");

-- CHECK
ALTER TABLE "channels"
    ADD CONSTRAINT "CHECK_CHANNELS_TYPE"
        CHECK ("type" IN ('PUBLIC', 'PRIVATE'));

-- FK 제약조건들 재 설정 ( 삭제 - 재생성)

BEGIN;
ALTER TABLE "user_statuses"
    DROP CONSTRAINT "FK_users_TO_user_statuses_1";
ALTER TABLE "user_statuses"
    ADD CONSTRAINT "FK_users_TO_user_statuses_1"
        FOREIGN KEY ("user_id") REFERENCES "users" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "read_statuses"
    DROP CONSTRAINT "FK_users_TO_read_statuses_1";
ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_users_TO_read_statuses_1"
        FOREIGN KEY ("user_id") REFERENCES "users" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "messages"
    DROP CONSTRAINT "FK_users_TO_messages_1";
ALTER TABLE "messages"
    ADD CONSTRAINT "FK_users_TO_messages_1"
        FOREIGN KEY ("author_id") REFERENCES "users" ("id")
            ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "read_statuses"
    DROP CONSTRAINT "FK_channels_TO_read_statuses_1";
ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_channels_TO_read_statuses_1"
        FOREIGN KEY ("channel_id") REFERENCES "channels" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "messages"
    DROP CONSTRAINT "FK_channels_TO_messages_1";
ALTER TABLE "messages"
    ADD CONSTRAINT "FK_channels_TO_messages_1"
        FOREIGN KEY ("channel_id") REFERENCES "channels" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "users"
    DROP CONSTRAINT "FK_binary_contents_TO_users_1";
ALTER TABLE "users"
    ADD CONSTRAINT "FK_binary_contents_TO_users_1"
        FOREIGN KEY ("profile_id") REFERENCES "binary_contents" ("id")
            ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "message_attachments"
    DROP CONSTRAINT "FK_messages_TO_message_attachments_1";
ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_messages_TO_message_attachments_1"
        FOREIGN KEY ("message_id") REFERENCES "messages" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;


BEGIN;
ALTER TABLE "message_attachments"
    DROP CONSTRAINT "FK_binary_contents_TO_message_attachments_1";
ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_binary_contents_TO_message_attachments_1"
        FOREIGN KEY ("attachment_id") REFERENCES "binary_contents" ("id")
            ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;

BEGIN;
ALTER TABLE binary_contents
    DROP COLUMN bytes;

COMMIT;

ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

BEGIN ;
ALTER TABLE binary_contents
    ADD COLUMN updated_at timestamp with time zone;

ALTER TABLE binary_contents
    ADD COLUMN status varchar(20) NOT NULL DEFAULT 'PROCESSING';

UPDATE binary_contents
SET status = 'SUCCESS'
WHERE created_at < NOW();
COMMIT ;

BEGIN ;
ALTER TABLE read_statuses
    ADD COLUMN notification_enabled boolean NOT NULL DEFAULT false;
COMMIT ;