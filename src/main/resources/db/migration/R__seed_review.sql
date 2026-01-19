INSERT INTO review (
    member_id,
    product_id,
    content,
    rating,
    like_count,
    thumbnail_img,
    created_at,
    updated_at
)
VALUES
-- INTERNAL-test1 (누다 소형)에 대한 리뷰
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
(SELECT id FROM product WHERE external_product_id = 'INTERNAL-test1'),
    '사이즈가 딱 적당하고 순면이라 피부 자극이 없어서 너무 좋아요.',
    5.0,
    3,
    'https://nuda.com/reviews/rev_test1_1.png',
    now(), now()
    ),

-- INTERNAL-test2 (누다 중형)에 대한 리뷰
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM product WHERE external_product_id = 'INTERNAL-test2'),
    '흡수력이 좋아서 일상생활 할 때 안심하고 사용합니다.',
    4.5,
    10,
    null,
    now(), now()
),

-- INTERNAL-test3 (누다 오버나이트)에 대한 리뷰
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM product WHERE external_product_id = 'INTERNAL-test3'),
    '길이가 넉넉해서 밤에 잘 때 뒤척여도 샘 걱정이 없네요.',
    5.0,
    5,
    'https://nuda.com/reviews/rev_test3_1.png',
    now(), now()
),

-- INTERNAL-test4 (클리나 팬티라이너)에 대한 리뷰
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM product WHERE external_product_id = 'INTERNAL-test4'),
    '너무 얇아서 착용한 느낌이 거의 안 나요. 데일리로 최고입니다.',
    4.0,
    2,
    null,
    now(), now()
),

-- INTERNAL-test5 (클리나 탐폰)에 대한 리뷰
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM product WHERE external_product_id = 'INTERNAL-test5'),
    '탐폰 입문자인데 삽입도 부드럽고 활동하기 편해서 만족합니다.',
    4.5,
    1,
    'https://nuda.com/reviews/rev_test5_1.png',
    now(), now()
),

-- INTERNAL-test5 (클리나 탐폰)에 대한 추가 리뷰 (중복 상품 리뷰 테스트용)
(
    (SELECT id FROM member WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM product WHERE external_product_id = 'INTERNAL-test5'),
    '운동할 때 필수템이에요. 운동복 입어도 티가 안 나서 좋습니다.',
    5.0,
    0,
    null,
    now(), now()
);