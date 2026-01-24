CREATE SEQUENCE seq_order_item_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE order_item (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_order_item_id'),
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    price INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX ix_order_item_order_id ON order_item(order_id);
CREATE INDEX ix_order_item_product_id ON order_item(product_id);
