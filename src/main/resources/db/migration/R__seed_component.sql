INSERT INTO component (name, content, layer_type, created_at, updated_at)
VALUES
    ('탑시트', '피부에 직접 닿는 최상단 시트', 'TOP_SHEET', now(), now()),
    ('흡수층', '혈을 흡수하고 저장하는 핵심 레이어', 'ABSORBER', now(), now()),
    ('백시트', '외부로의 샘 방지를 위한 방수층', 'BACK_SHEET', now(), now()),
    ('접착층', '속옷에 고정하기 위한 접착 레이어', 'ADHESIVE', now(), now()),
    ('윙', '측면 고정을 위한 날개 부분', 'WING', now(), now());
