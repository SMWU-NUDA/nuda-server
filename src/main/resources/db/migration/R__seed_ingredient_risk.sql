INSERT INTO ingredient_risk (ingredient_id, source, risk_level, toxicity, reference_url, created_at, updated_at)
SELECT
    i.id,
    'EWG',
    'DANGER',
    '피부 자극 가능성',
    'https://www.ewg.org',
    now(),
    now()
FROM ingredient i
WHERE i.name = '향료';
