-- 깨끗이 드롭(자식 → 부모 순)
drop table IF exists message_attachments cascade;
drop table IF exists read_statuses cascade;
drop table IF exists messages cascade;
drop table IF exists channels cascade;
drop table IF exists user_statuses cascade;
drop table IF exists users cascade;
drop table IF exists binary_contents cascade;

create table if not exists binary_contents
(
    id           uuid primary key      default gen_random_uuid(),
    created_at   timestamptz  not null default now(),
    file_name    varchar(255) not null,
    size         bigint       not null,
    content_type varchar(100) not null,
    bytes        bytea        not null
);

create table if not exists users
(
    id         uuid primary key      default gen_random_uuid(),
    created_at timestamptz  not null default now(),
    updated_at timestamptz,
    username   varchar(50)  not null,
    email      varchar(100) not null,
    password   varchar(60)  not null,
    profile_id uuid,

    constraint users_username_uk unique (username),
    constraint users_email_uk unique (email),
    constraint users_profile_fk foreign key (profile_id)
        references binary_contents (id) on delete set null
);

create table if not exists user_statuses
(
    id             uuid primary key     default gen_random_uuid(),
    created_at     timestamptz not null default now(),
    updated_at     timestamptz,
    user_id        uuid        not null,
    last_active_at timestamptz not null,

    constraint user_statuses_user_id_uk unique (user_id),
    constraint user_statuses_user_fk foreign key (user_id)
        references users (id) on delete cascade
);

create table if not exists channels
(
    id          uuid primary key     default gen_random_uuid(),
    created_at  timestamptz not null default now(),
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) not null check (type in ('PUBLIC', 'PRIVATE'))
);

create table if not exists messages
(
    id         uuid primary key     default gen_random_uuid(),
    created_at timestamptz not null default now(),
    updated_at timestamptz,
    content    text,
    channel_id uuid        not null,
    author_id  uuid,

    constraint messages_channel_id_fk foreign key (channel_id)
        references channels (id) on delete cascade,
    constraint messages_author_id_fk foreign key (author_id)
        references users (id) on delete set null
);

create table if not exists read_statuses
(
    id           uuid primary key     default gen_random_uuid(),
    created_at   timestamptz not null default now(),
    updated_at   timestamptz,
    user_id      uuid,
    channel_id   uuid,
    last_read_at timestamptz not null,

    constraint read_statuses_user_channel_uk unique (user_id, channel_id),
    constraint read_statuses_user_id_fk foreign key (user_id)
        references users (id) on delete cascade,
    constraint read_statuses_channel_id_fk foreign key (channel_id)
        references channels (id) on delete cascade
);

create table if not exists message_attachments
(
    message_id    uuid,
    attachment_id uuid,

    constraint message_attachments_message_id_fk foreign key (message_id)
        references messages (id) on delete cascade,
    constraint message_attachments_attachment_id_fk foreign key (attachment_id)
        references binary_contents (id) on delete cascade
);