CREATE SEQUENCE cart_item_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY DEFAULT nextval('cart_item_seq'),
    cart_id BIGINT NOT NULL,
    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT ck_cart_item_quantity CHECK (quantity > 0),
    CONSTRAINT uk_cart_item_cart_product
        UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_item_cart_id ON cart_item (cart_id);
CREATE INDEX idx_cart_item_product_id ON cart_item (product_id);
