INSERT INTO product (
    external_product_id,
    brand_id,
    category_id,
    name,
    cost_price, discount_rate,
    content,
    thumbnail_img,
    average_rating, review_count, like_count, sales_count, view_count,
    created_at, updated_at
)
VALUES
(
    'INTERNAL-test1',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'PUTSMALL'),
    '누다 입는 생리대 소형',
    9900, 0,
    '편안한 착용감의 팬티형 소형 제품입니다.',
    'https://nuda.com/products/put_small.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test2',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'PUTMEDIUM'),
    '누다 입는 생리대 중형',
    10900, 0,
    '활동량이 많은 날을 위한 팬티형 중형 제품입니다.',
    'https://nuda.com/products/put_medium.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test3',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'PUTLARGE'),
    '누다 입는 생리대 대형',
    11900, 0,
    '흡수력이 강화된 팬티형 대형 제품입니다.',
    'https://nuda.com/products/put_large.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test4',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'PUTOVERNIGHT'),
    '누다 입는 생리대 오버나이트',
    12900, 0,
    '밤에도 안심하고 사용할 수 있는 팬티형 오버나이트 제품입니다.',
    'https://nuda.com/products/put_overnight.png',
    0,0,0,0,0,
    now(), now()
),

(
    'INTERNAL-test5',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'TAMPOSMALL'),
    '클리나 탐폰 소형',
    6500, 0,
    '가벼운 사용감을 위한 소형 탐폰입니다.',
    'https://nuda.com/products/tampon_small.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test6',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'TAMPOMEDIUM'),
    '클리나 탐폰 중형',
    7500, 0,
    '일반 사용에 적합한 중형 탐폰입니다.',
    'https://nuda.com/products/tampon_medium.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test7',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'TAMPOLARGE'),
    '클리나 탐폰 대형',
    8500, 0,
    '흡수력이 강화된 대형 탐폰입니다.',
    'https://nuda.com/products/tampon_large.png',
    0,0,0,0,0,
    now(), now()
),

(
    'INTERNAL-test8',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'LINER'),
    '클리나 데일리 팬티라이너',
    5200, 0,
    '얇고 가벼운 착용감의 팬티라이너입니다.',
    'https://nuda.com/products/liner.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test9',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'SMALL'),
    '누다 순면 생리대 소형',
    6900, 0,
    '민감한 피부를 위한 순면 소형 생리대입니다.',
    'https://nuda.com/products/pad_small.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test10',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'MEDIUM'),
    '누다 순면 생리대 중형',
    7900, 0,
    '일상 사용에 적합한 중형 생리대입니다.',
    'https://nuda.com/products/pad_medium.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test11',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'LARGE'),
    '누다 순면 생리대 대형',
    8900, 0,
    '흡수력이 강화된 대형 생리대입니다.',
    'https://nuda.com/products/pad_large.png',
    0,0,0,0,0,
    now(), now()
),
(
    'INTERNAL-test12',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'OVERNIGHT'),
    '누다 오버나이트 생리대',
    9900, 0,
    '밤에도 안심하고 사용할 수 있는 오버나이트 제품입니다.',
    'https://nuda.com/products/pad_overnight.png',
    0,0,0,0,0,
    now(), now()
);
