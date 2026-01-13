CREATE TABLE product_like (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_like_seq'),
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_product_like_member
        FOREIGN KEY (member_id) REFERENCES member(id),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_product_like_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uk_product_like_member_product
        UNIQUE (member_id, product_id)
);

CREATE INDEX idx_product_like_product_id ON product_like(product_id);
