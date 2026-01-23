-- TOP_SHEET
INSERT INTO product_ingredient (product_id, component_id, ingredient_id, created_at, updated_at)
SELECT
    p.id,
    c.id,
    i.id,
    now(),
    now()
FROM product p
         JOIN component c ON c.layer_type = 'TOP_SHEET'
         JOIN ingredient i ON i.name IN ('순면', '향료');

-- ABSORBER
INSERT INTO product_ingredient (product_id, component_id, ingredient_id, created_at, updated_at)
SELECT
    p.id,
    c.id,
    i.id,
    now(),
    now()
FROM product p
         JOIN component c ON c.layer_type = 'ABSORBER'
         JOIN ingredient i ON i.name = '폴리아크릴산나트륨';

-- BACK_SHEET
INSERT INTO product_ingredient (product_id, component_id, ingredient_id, created_at, updated_at)
SELECT
    p.id,
    c.id,
    i.id,
    now(),
    now()
FROM product p
         JOIN component c ON c.layer_type = 'BACK_SHEET'
         JOIN ingredient i ON i.name = '폴리에틸렌';

-- ADHESIVE
INSERT INTO product_ingredient (product_id, component_id, ingredient_id, created_at, updated_at)
SELECT
    p.id,
    c.id,
    i.id,
    now(),
    now()
FROM product p
         JOIN component c ON c.layer_type = 'ADHESIVE'
         JOIN ingredient i ON i.name = '접착제';
