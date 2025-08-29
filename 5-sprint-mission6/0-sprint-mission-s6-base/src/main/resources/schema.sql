-- ===== 확장: UUID 생성 함수 (DB에서 UUID 자동 발급)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===== updated_at 자동 갱신 트리거 함수
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================================
-- 1) 파일 메타: binary_contents
-- =========================================================
CREATE TABLE IF NOT EXISTS binary_contents (
  id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at   timestamptz NOT NULL DEFAULT now(),
  file_name    varchar(255) NOT NULL,
  size         bigint       NOT NULL CHECK (size >= 0),
  content_type varchar(100) NOT NULL,
  bytes        bytea        NOT NULL
);

-- =========================================================
-- 2) 사용자: users  (profile_id → binary_contents.id, ON DELETE SET NULL)
-- =========================================================
CREATE TABLE IF NOT EXISTS users (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  username   varchar(50)  NOT NULL,
  email      varchar(100) NOT NULL,
  password   varchar(60)  NOT NULL,             -- 해시 저장 가정
  profile_id uuid,
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email    UNIQUE (email),
  CONSTRAINT fk_users_profile
    FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

CREATE TRIGGER trg_users_updated
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =========================================================
-- 3) 채널: channels (type: PUBLIC | PRIVATE)
-- =========================================================
CREATE TABLE IF NOT EXISTS channels (
  id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  name         varchar(100) NOT NULL,
  description  varchar(500),
  type         varchar(10)  NOT NULL CHECK (type IN ('PUBLIC','PRIVATE'))
);

CREATE TRIGGER trg_channels_updated
BEFORE UPDATE ON channels
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =========================================================
-- 4) 메시지: messages
--    channel_id → channels.id (CASCADE)
--    author_id  → users.id    (SET NULL)
-- =========================================================
CREATE TABLE IF NOT EXISTS messages (
  id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  content    text        NOT NULL,
  channel_id uuid        NOT NULL,
  author_id  uuid,
  CONSTRAINT fk_messages_channel
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
  CONSTRAINT fk_messages_author
    FOREIGN KEY (author_id)  REFERENCES users(id)    ON DELETE SET NULL
);

CREATE TRIGGER trg_messages_updated
BEFORE UPDATE ON messages
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 메시지 조회 성능 인덱스(채널별 최신순)
CREATE INDEX IF NOT EXISTS idx_messages_channel_created_at
  ON messages (channel_id, created_at DESC);

-- =========================================================
-- 5) 메시지 첨부: message_attachments
--    message_id → messages.id (CASCADE)
--    attachment_id → binary_contents.id (CASCADE)
-- =========================================================
CREATE TABLE IF NOT EXISTS message_attachments (
  message_id     uuid NOT NULL,
  attachment_id  uuid NOT NULL,
  PRIMARY KEY (message_id, attachment_id),
  CONSTRAINT fk_ma_message
    FOREIGN KEY (message_id)    REFERENCES messages(id)        ON DELETE CASCADE,
  CONSTRAINT fk_ma_attachment
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);

-- =========================================================
-- 6) 사용자 상태: user_statuses
--    user_id 단일행 보장(UNIQUE), 유저 삭제 시 함께 삭제(CASCADE)
-- =========================================================
CREATE TABLE IF NOT EXISTS user_statuses (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  user_id       uuid        NOT NULL UNIQUE,
  last_active_at timestamptz NOT NULL,
  CONSTRAINT fk_us_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_user_statuses_updated
BEFORE UPDATE ON user_statuses
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =========================================================
-- 7) 읽음 상태: read_statuses
--    (user_id, channel_id) 유니크, 유저/채널 삭제 시 함께 삭제(CASCADE)
-- =========================================================
CREATE TABLE IF NOT EXISTS read_statuses (
  id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  user_id      uuid        NOT NULL,
  channel_id   uuid        NOT NULL,
  last_read_at timestamptz NOT NULL,
  CONSTRAINT fk_rs_user
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
  CONSTRAINT fk_rs_channel
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
  CONSTRAINT uq_rs_user_channel UNIQUE (user_id, channel_id)
);

CREATE TRIGGER trg_read_statuses_updated
BEFORE UPDATE ON read_statuses
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 조회 보조 인덱스
CREATE INDEX IF NOT EXISTS idx_read_statuses_user ON read_statuses (user_id);
