-- INTERNAL-test1
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Aloe Vera Extract') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Wood Pulp','Polyacrylate') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Polyethylene Film','Breathable Microporous Film') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Chamomile Extract','Vitamin E Coating') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Pressure Sensitive Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test1';

-- INTERNAL-test2
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Polypropylene Fiber','Rayon Blend') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Bamboo Fiber') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Biodegradable Film','Printed Plastic Sheet') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Chamomile Extract','Cooling Menthol') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Latex Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test2';

-- INTERNAL-test3
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Dye Pigment') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Wood Pulp','Chlorine Bleached Pulp') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Polyethylene Film','PVC Layer') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Paraben Preservative','Herbal Complex') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Pressure Sensitive Adhesive','Solvent Based Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test3';

-- INTERNAL-test4
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Fragrance Coating') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Synthetic Gel Core') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Breathable Microporous Film','PVC Layer') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Vitamin E Coating','Silver Nano Particle') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Synthetic Resin Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test4';

-- INTERNAL-test5
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Rayon Blend','Polypropylene Fiber') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Wood Pulp','Polyacrylate') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Biodegradable Film','Printed Plastic Sheet') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Chamomile Extract','Herbal Complex') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Latex Adhesive','Pressure Sensitive Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test5';

-- INTERNAL-test6
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Dye Pigment') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Bamboo Fiber') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Polyethylene Film','Biodegradable Film') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Cooling Menthol','Vitamin E Coating') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Latex Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test6';

-- INTERNAL-test7
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Polypropylene Fiber','Fragrance Coating') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Chlorine Bleached Pulp','Synthetic Gel Core') AND i.layer_type='ABSORBER')
        OR (i.name IN ('PVC Layer','Printed Plastic Sheet') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Paraben Preservative','Silver Nano Particle') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Solvent Based Glue','Synthetic Resin Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test7';

-- INTERNAL-test8
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Polypropylene Fiber') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Wood Pulp','Bamboo Fiber') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Breathable Microporous Film','Biodegradable Film') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Chamomile Extract','Vitamin E Coating') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Pressure Sensitive Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test8';

-- INTERNAL-test9
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Aloe Vera Extract','Dye Pigment') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Polyacrylate') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Polyethylene Film','Printed Plastic Sheet') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Chamomile Extract','Cooling Menthol') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Latex Adhesive','Synthetic Resin Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test9';

-- INTERNAL-test10
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Polypropylene Fiber','Rayon Blend') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Wood Pulp','Super Absorbent Polymer','Synthetic Gel Core') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Biodegradable Film','PVC Layer') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Herbal Complex','Vitamin E Coating') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Hot Melt Adhesive','Solvent Based Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test10';

-- INTERNAL-test11
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Fragrance Coating') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Chlorine Bleached Pulp','Polyacrylate','Bamboo Fiber') AND i.layer_type='ABSORBER')
        OR (i.name IN ('Polyethylene Film','Breathable Microporous Film') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Paraben Preservative','Silver Nano Particle') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Pressure Sensitive Adhesive','Latex Adhesive') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test11';

-- INTERNAL-test12
INSERT INTO product_ingredient (product_id, external_product_id, ingredient_id)
SELECT p.id, p.external_product_id, i.id
FROM product p
         JOIN ingredient i ON (
    (i.name IN ('Organic Cotton','Rayon Blend','Dye Pigment') AND i.layer_type='TOP_SHEET')
        OR (i.name IN ('Super Absorbent Polymer','Chlorine Bleached Pulp') AND i.layer_type='ABSORBER')
        OR (i.name IN ('PVC Layer','Printed Plastic Sheet') AND i.layer_type='BACK_SHEET')
        OR (i.name IN ('Cooling Menthol','Herbal Complex') AND i.layer_type='ADDITIVE')
        OR (i.name IN ('Synthetic Resin Glue','Solvent Based Glue') AND i.layer_type='ADHESIVE')
    )
WHERE p.external_product_id='INTERNAL-test12';

