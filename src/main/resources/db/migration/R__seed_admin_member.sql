INSERT INTO member (
    nickname, username, password, email,
    profile_img,
    role,
    signup_step, status,
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
        'COMPLETED',
        'ACTIVE',
        '00000',
        'ex address1',
        'ex address 2',
        '010-0000-0000',
        '클리나',
        now(),
        now()
);
