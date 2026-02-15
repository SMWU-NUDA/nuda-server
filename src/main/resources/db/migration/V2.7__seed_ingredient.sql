INSERT INTO ingredient (name, risk_level, layer_type, content)
VALUES

-- TOP_SHEET (표지)
('Organic Cotton', 'SAFE', 'TOP_SHEET', '유기농 면 섬유'),
('Polypropylene Fiber', 'SAFE', 'TOP_SHEET', '합성 부직포 섬유'),
('Rayon Blend', 'WARN', 'TOP_SHEET', '레이온 혼합 섬유'),
('Fragrance Coating', 'DANGER', 'TOP_SHEET', '향료 코팅 처리'),
('Aloe Vera Extract', 'SAFE', 'TOP_SHEET', '알로에 추출물 코팅'),
('Dye Pigment', 'WARN', 'TOP_SHEET', '색상 표현용 안료'),

-- ABSORBER (흡수체)
('Super Absorbent Polymer', 'SAFE', 'ABSORBER', '고흡수성 수지'),
('Wood Pulp', 'SAFE', 'ABSORBER', '목재 펄프'),
('Chlorine Bleached Pulp', 'DANGER', 'ABSORBER', '염소 표백 펄프'),
('Polyacrylate', 'WARN', 'ABSORBER', '폴리아크릴레이트 흡수체'),
('Bamboo Fiber', 'SAFE', 'ABSORBER', '대나무 섬유'),
('Synthetic Gel Core', 'UNKNOWN', 'ABSORBER', '합성 젤 흡수체'),

-- BACK_SHEET (방수층)
('Polyethylene Film', 'SAFE', 'BACK_SHEET', '폴리에틸렌 방수 필름'),
('Breathable Microporous Film', 'SAFE', 'BACK_SHEET', '통기성 미세공 필름'),
('PVC Layer', 'DANGER', 'BACK_SHEET', 'PVC 방수층'),
('Biodegradable Film', 'SAFE', 'BACK_SHEET', '생분해성 필름'),
('Printed Plastic Sheet', 'WARN', 'BACK_SHEET', '프린팅 플라스틱 시트'),

-- ADDITIVE (기타)
('Chamomile Extract', 'SAFE', 'ADDITIVE', '카모마일 추출물'),
('Silver Nano Particle', 'WARN', 'ADDITIVE', '은 나노 입자'),
('Paraben Preservative', 'DANGER', 'ADDITIVE', '파라벤 보존제'),
('Vitamin E Coating', 'SAFE', 'ADDITIVE', '비타민 E 코팅'),
('Cooling Menthol', 'WARN', 'ADDITIVE', '멘톨 성분'),
('Herbal Complex', 'UNKNOWN', 'ADDITIVE', '복합 허브 추출물'),

-- ADHESIVE (접착제)
('Hot Melt Adhesive', 'SAFE', 'ADHESIVE', '핫멜트 접착제'),
('Pressure Sensitive Adhesive', 'SAFE', 'ADHESIVE', '감압 접착제'),
('Latex Adhesive', 'WARN', 'ADHESIVE', '라텍스 접착제'),
('Synthetic Resin Glue', 'UNKNOWN', 'ADHESIVE', '합성 수지 접착제'),
('Solvent Based Glue', 'DANGER', 'ADHESIVE', '용제 기반 접착제');
