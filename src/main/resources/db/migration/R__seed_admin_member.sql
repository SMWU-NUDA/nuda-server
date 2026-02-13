-- 테스트용 관리자 계정
INSERT INTO member (
    nickname, username, password, email,
    profile_img,
    role, status,
    postal_code, address1, address2, phone_num, recipient,
    created_at, updated_at
)
VALUES (
        '관리자',
        'admin',
        '$2a$10$KXp6.fccEUG0TP8aS3ihju2zqhWvr6PHhwsyoscUnxpAAx2FOISNW',
        'admin@a.com',
        'ex imgUrl',
        'ADMIN',
        'ACTIVE',
        '00000',
        'ex address1',
        'ex address 2',
        '010-0000-0000',
        '클리나',
        now(),
        now()
);

INSERT INTO keyword (
    member_id,
    irritation_level,
    scent,
    change_frequency,
    thickness,
    priority,
    created_at,
    updated_at
)
SELECT
    m.id,
    'SOMETIMES',
    'MILD',
    'HIGH',
    'NORMAL',
    'SAFETY',
    now(),
    now()
FROM member m
WHERE m.username = 'admin';

INSERT INTO cart (member_id)
SELECT id FROM member WHERE username = 'admin';

-- csv 대량 업로드 용 계정
INSERT INTO member (
    nickname, username, password, email,
    profile_img,
    role, status,
    postal_code, address1, address2, phone_num, recipient,
    created_at, updated_at
)
VALUES (
           '정보 없음',
           'csvAdmin',
           '$2a$10$KXp6.fccEUG0TP8aS3ihju2zqhWvr6PHhwsyoscUnxpAAx2FOISNW',
           'csvAdmin@a.com',
           'ex imgUrl',
           'ADMIN',
           'ACTIVE',
           '00000',
           'ex address1',
           'ex address 2',
           '010-0000-0000',
           '클리나',
           now(),
           now()
);
