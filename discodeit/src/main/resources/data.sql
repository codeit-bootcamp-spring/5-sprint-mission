INSERT INTO users (id, created_at, updated_at, username, email, password, profile_id)
VALUES ('11111111-1111-1111-1111-111111111111', now(), now(),
        'testuser', 'testuser@email.com',
        '$2a$10$abcdefghijklmnopqrstuvwx1234567890abcdefghi', -- bcrypt 예시
        NULL);

-- 2. 채널(Channel, public)
INSERT INTO channels (id, created_at, updated_at, name, description, type)
VALUES ('22222222-2222-2222-2222-222222222222', now(), now(),
        'general', 'Public general channel', 'PUBLIC');

-- 3. 메시지(Message)
INSERT INTO messages (id, created_at, updated_at, content, channel_id, author_id)
VALUES ('33333333-3333-3333-3333-333333333333', now(), now(),
        'Hello, this is the first message!',
        '22222222-2222-2222-2222-222222222222',
        '11111111-1111-1111-1111-111111111111');