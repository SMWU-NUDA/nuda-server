CREATE SEQUENCE seq_order_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE orders (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_order_id'),
    member_id BIGINT NOT NULL,
    order_num BIGINT NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_orders_order_num ON orders(order_num);
CREATE INDEX ix_orders_member_id ON orders(member_id);
