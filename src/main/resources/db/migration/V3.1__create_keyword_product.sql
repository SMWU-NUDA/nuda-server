CREATE SEQUENCE keyword_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE keyword_product (
    id BIGINT PRIMARY KEY DEFAULT nextval('keyword_product_seq'),
    keyword_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_keyword_product_keyword
        FOREIGN KEY (keyword_id) REFERENCES keyword(id) ON DELETE CASCADE,
    CONSTRAINT fk_keyword_product_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT uq_keyword_product UNIQUE (keyword_id, product_id)
);

CREATE INDEX idx_keyword_product_product_id
    ON keyword_product(product_id);
