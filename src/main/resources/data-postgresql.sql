CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

WITH
-- 1) 유저 3명 (프로필 이미지 없음 → profile_id = NULL)
u AS (
    INSERT INTO users (id, created_at, updated_at, username, email, password, profile_id)
        VALUES (gen_random_uuid(), NOW(), NULL, 'alice', 'alice@example.com',
                '12341234', NULL),
               (gen_random_uuid(), NOW(), NULL, 'bob', 'bob@example.com',
                '12341234', NULL),
               (gen_random_uuid(), NOW(), NULL, 'charlie', 'charlie@example.com',
                '12341234', NULL),
               (gen_random_uuid(), now(), null, 'test', 'test@email.com',
                '$2a$10$B3mSgHyUmpbDvOYgYVjJee3fz4MDBtvp0KDppmaRO6hMWJFz/XDYW', null)
        RETURNING id, username),

-- 2) 유저 상태 (선택: 3명 모두 생성)
us AS (
    INSERT INTO user_statuses (id, created_at, updated_at, user_id, last_active_at)
        SELECT gen_random_uuid(), NOW(), NULL, id, NOW()
        FROM u
        RETURNING id),

-- 3) 채널 2개 (type은 자유 문자열. 예: PUBLIC)
ch AS (
    INSERT INTO channels (id, created_at, updated_at, name, description, type)
        VALUES (gen_random_uuid(), NOW(), NULL, 'general', '일반 대화 채널', 'PUBLIC'),
               (gen_random_uuid(), NOW(), NULL, 'random', '잡담용 채널', 'PUBLIC')
        RETURNING id, name),

-- 4) 메시지 4개 (첨부 없음, author는 유저 중 한 명)
msg AS (
    INSERT INTO messages (id, created_at, updated_at, content, channel_id, author_id)
        VALUES (gen_random_uuid(), NOW(), NULL,
                '안녕! 프로젝트 시작해볼까?',
                (SELECT id FROM ch WHERE name = 'general'),
                (SELECT id FROM u WHERE username = 'alice')),
               (gen_random_uuid(), NOW(), NULL,
                '좋아, 오늘 할 일부터 정리하자.',
                (SELECT id FROM ch WHERE name = 'general'),
                (SELECT id FROM u WHERE username = 'bob')),
               (gen_random_uuid(), NOW(), NULL,
                '커피 한 잔 하고 올게 ☕',
                (SELECT id FROM ch WHERE name = 'random'),
                (SELECT id FROM u WHERE username = 'charlie')),
               (gen_random_uuid(), NOW(), NULL,
                '내일 10시에 스탠드업 미팅 하자!',
                (SELECT id FROM ch WHERE name = 'random'),
                (SELECT id FROM u WHERE username = 'alice'))
        RETURNING id),

-- 5) 읽음 상태 (user_id + channel_id 유니크)
rs AS (
    INSERT INTO read_statuses (id, created_at, updated_at, user_id, channel_id, last_read_at)
        VALUES (gen_random_uuid(), NOW(), NULL,
                (SELECT id FROM u WHERE username = 'alice'),
                (SELECT id FROM ch WHERE name = 'general'),
                NOW()),
               (gen_random_uuid(), NOW(), NULL,
                (SELECT id FROM u WHERE username = 'bob'),
                (SELECT id FROM ch WHERE name = 'general'),
                NOW()),
               (gen_random_uuid(), NOW(), NULL,
                (SELECT id FROM u WHERE username = 'bob'),
                (SELECT id FROM ch WHERE name = 'random'),
                NOW())
        RETURNING id)
SELECT 'OK (seeded without binary_contents & attachments)' AS status;

COMMIT;