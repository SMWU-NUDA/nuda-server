INSERT INTO ingredient (name, risk_level, content, created_at, updated_at)
VALUES
    ('순면', 'SAFE', '자연 유래의 부드러운 섬유 소재', now(), now()),
    ('폴리아크릴산나트륨', 'WARN', '고흡수성 폴리머 성분', now(), now()),
    ('접착제', 'DANGER', '제품 고정을 위한 합성 접착 성분', now(), now()),
    ('폴리에틸렌', 'SAFE', '방수 기능을 위한 합성 소재', now(), now()),
    ('향료', 'DANGER', '제품 향을 위한 첨가 성분', now(), now());
