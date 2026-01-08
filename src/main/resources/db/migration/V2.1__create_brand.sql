CREATE TABLE brand (
    id BIGINT PRIMARY KEY DEFAULT nextval('brand_seq'),
    name VARCHAR(255) NOT NULL,
    logo_img TEXT,
    like_count INT DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
