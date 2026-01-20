CREATE SEQUENCE brand_like_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE brand_like (
    id BIGINT PRIMARY KEY DEFAULT nextval('brand_like_seq'),
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_brand_like_member
        FOREIGN KEY (member_id) REFERENCES member(id),
    brand_id BIGINT NOT NULL,
    CONSTRAINT fk_brand_like_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uk_brand_like_member_brand
        UNIQUE (member_id, brand_id)
);

CREATE INDEX idx_brand_like_brand_id ON brand_like(brand_id);
