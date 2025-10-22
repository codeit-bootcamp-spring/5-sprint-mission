-- H2 테스트 시드 (src/test/resources/seed.sql 추천)

-- (선택) H2를 PostgreSQL 호환 모드로
SET mode PostgreSQL;

-- 깨끗하게 초기화
SET referential_integrity FALSE;

TRUNCATE TABLE message_attachments;
TRUNCATE TABLE read_statuses;
TRUNCATE TABLE messages;
TRUNCATE TABLE user_statuses;
TRUNCATE TABLE users;
TRUNCATE TABLE channels;
-- binary_contents는 이번 테스트에 사용 안 함

SET referential_integrity TRUE;

-- =============================
-- Users
-- =============================
INSERT INTO users (id, created_at, updated_at, username, email, password, profile_id)
VALUES ('11111111-1111-1111-1111-111111111111', '2025-01-01 00:00:00+00', NULL, 'user1',
        'user1@test.com', '{noop}pw1', NULL),
       ('22222222-2222-2222-2222-222222222222', '2025-01-01 00:00:00+00', NULL, 'user2',
        'user2@test.com', '{noop}pw2', NULL);

-- =============================
-- User Statuses
-- =============================
INSERT INTO user_statuses (id, created_at, updated_at, user_id, last_active_at)
VALUES ('31111111-1111-1111-1111-111111111111', '2025-01-01 00:00:00+00', NULL,
        '11111111-1111-1111-1111-111111111111', '2025-01-01 00:00:10+00'),
       ('32222222-2222-2222-2222-222222222222', '2025-01-01 00:00:00+00', NULL,
        '22222222-2222-2222-2222-222222222222', '2025-01-01 00:00:20+00');

-- =============================
-- Channels
-- =============================
INSERT INTO channels (id, created_at, updated_at, name, description, type)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '2025-01-01 00:00:00+00', NULL, 'ch-public',
        '공개 채널', 'PUBLIC'),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002', '2025-01-01 00:00:00+00', NULL, 'ch-private',
        '비공개 채널', 'PRIVATE');

-- =============================
-- Messages (ch-public: m1~m5 / ch-private: p1~p2)
-- createdAt 내림차순 + id 내림차순 테스트하기 좋게 타임스탬프 분리
-- =============================
INSERT INTO messages (id, created_at, updated_at, content, channel_id, author_id)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0001', '2025-01-01 00:00:00+00', NULL, 'm1',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0002', '2025-01-01 00:00:01+00', NULL, 'm2',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0003', '2025-01-01 00:00:02+00', NULL, 'm3',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0004', '2025-01-01 00:00:03+00', NULL, 'm4',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0005', '2025-01-01 00:00:04+00', NULL, 'm5',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),

       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0011', '2025-01-02 00:00:00+00', NULL, 'p1',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002', '22222222-2222-2222-2222-222222222222'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0012', '2025-01-02 00:00:01+00', NULL, 'p2',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002', '22222222-2222-2222-2222-222222222222');

-- (선택) created_at 동률일 때 id desc 타이브레이크 테스트용 더미 2건
-- 동일 시각, 서로 다른 id
-- INSERT INTO messages (...) VALUES
-- ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0006', '2025-01-01 00:00:05+00', NULL, 'm6', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111'),
-- ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0007', '2025-01-01 00:00:05+00', NULL, 'm7', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111');

-- =============================
-- Read Statuses
-- =============================
INSERT INTO read_statuses (id, created_at, updated_at, user_id, channel_id, last_read_at)
VALUES ('cccccccc-cccc-cccc-cccc-cccccccc0001', '2025-01-01 00:00:00+00', NULL,
        '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        '2025-01-01 00:00:02+00'),
       ('cccccccc-cccc-cccc-cccc-cccccccc0002', '2025-01-01 00:00:00+00', NULL,
        '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        '2025-01-01 00:00:01+00');
