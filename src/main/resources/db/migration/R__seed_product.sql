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
-- 누다 브랜드A / 소형
(
    'INTERNAL-test1',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'SMALL'),
    '누다 순면 생리대 소형 20P',
    6900, 0,
    '민감한 피부를 위한 순면 소재의 소형 생리대입니다.',
    'https://nuda.com/products/nuda_small_20p.png',
    0, 0, 0, 0, 0,
    now(), now()
),

-- 누다 브랜드A / 중형
(
    'INTERNAL-test2',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'MEDIUM'),
    '누다 순면 생리대 중형 18P',
    7900, 0,
    '일상 사용에 적합한 흡수력의 중형 생리대입니다.',
    'https://nuda.com/products/nuda_medium_18p.png',
    0, 0, 0, 0, 0,
    now(), now()
),

-- 누다 브랜드A / 오버나이트
(
    'INTERNAL-test3',
    (SELECT id FROM brand WHERE name = '누다 브랜드A'),
    (SELECT id FROM category WHERE code = 'OVERNIGHT'),
    '누다 오버나이트 생리대 10P',
    8900, 0,
    '밤에도 안심하고 사용할 수 있는 오버나이트 제품입니다.',
    'https://nuda.com/products/nuda_overnight_10p.png',
    0, 0, 0, 0, 0,
    now(), now()
),

-- 클리나 브랜드B / 팬티라이너
(
    'INTERNAL-test4',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'LINER'),
    '클리나 데일리 팬티라이너 30P',
    5200, 0,
    '얇고 가벼운 착용감의 데일리 팬티라이너입니다.',
    'https://nuda.com/products/clina_liner_30p.png',
    0, 0, 0, 0, 0,
    now(), now()
),

-- 클리나 브랜드B / 탐폰
(
    'INTERNAL-test5',
    (SELECT id FROM brand WHERE name = '클리나 브랜드B'),
    (SELECT id FROM category WHERE code = 'TAMPON'),
    '클리나 탐폰 레귤러',
    7500, 0,
    '활동량이 많은 날에도 안정적인 흡수력을 제공합니다.',
    'https://nuda.com/products/clina_tampon_regular.png',
    0, 0, 0, 0, 0,
    now(), now()
);
