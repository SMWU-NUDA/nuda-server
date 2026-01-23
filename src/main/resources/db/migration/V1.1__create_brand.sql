CREATE SEQUENCE brand_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE brand (
    id BIGINT PRIMARY KEY DEFAULT nextval('brand_seq'),
    name VARCHAR(255) NOT NULL,
    logo_img TEXT,
    like_count INT DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
