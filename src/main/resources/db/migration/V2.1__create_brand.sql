CREATE TABLE brand (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_img TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
