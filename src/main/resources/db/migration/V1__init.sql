CREATE TABLE flyway_test (
                             id BIGSERIAL PRIMARY KEY,
                             message VARCHAR(100),
                             created_at TIMESTAMP DEFAULT now()
);
