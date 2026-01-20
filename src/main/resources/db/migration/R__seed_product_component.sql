INSERT INTO product_component (product_id, component_id, created_at, updated_at)
SELECT
    p.id,
    c.id,
    now(),
    now()
FROM product p
         JOIN component c
              ON c.layer_type IN ('TOP_SHEET', 'ABSORBER', 'BACK_SHEET', 'ADHESIVE');
