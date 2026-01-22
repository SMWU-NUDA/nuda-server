CREATE SEQUENCE cart_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE cart (
    id BIGINT PRIMARY KEY DEFAULT nextval('cart_seq'),
    member_id BIGINT NOT NULL,

    CONSTRAINT uk_cart_member UNIQUE (member_id)
);
