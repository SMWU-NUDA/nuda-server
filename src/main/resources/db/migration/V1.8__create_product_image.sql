CREATE SEQUENCE product_image_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE product_image (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_image_seq'),
    product_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    sequence INT,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_image_product_id
    ON product_image(product_id);
