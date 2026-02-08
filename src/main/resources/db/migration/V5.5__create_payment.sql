CREATE SEQUENCE seq_payment_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE payment (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_payment_id'),
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    payment_key VARCHAR(100) NOT NULL,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    approved_at  TIMESTAMP
);

CREATE UNIQUE INDEX ux_payment_payment_key ON payment(payment_key);
CREATE UNIQUE INDEX ux_payment_order_id ON payment(order_id);
