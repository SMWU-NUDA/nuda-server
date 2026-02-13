CREATE TYPE rec_event_type AS ENUM (
    'CART',
    'ADD',
    'CART_REMOVE',
    'PURCHASE',
    'CANCELED'
);

CREATE SEQUENCE rec_event_log_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE rec_event_log (
    id BIGINT PRIMARY KEY DEFAULT nextval('rec_event_log_seq'),
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_rec_event_log_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_rec_event_log_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    external_product_id TEXT NOT NULL,
    event_type rec_event_type NOT NULL,
    quantity INT NOT NULL,
    occurred_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_rec_event_log_member ON rec_event_log(member_id);
CREATE INDEX idx_rec_event_log_product ON rec_event_log(product_id);
