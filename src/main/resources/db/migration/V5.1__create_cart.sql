CREATE SEQUENCE cart_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE cart (
    id BIGINT PRIMARY KEY DEFAULT nextval('cart_seq'),
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uk_cart_member UNIQUE (member_id),
    CONSTRAINT fk_cart_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);
