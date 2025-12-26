CREATE TABLE product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    sequence INT,
    type image_type NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_product_image_updated_at
    BEFORE UPDATE ON product_image
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_product_image_product_id
    ON product_image(product_id);