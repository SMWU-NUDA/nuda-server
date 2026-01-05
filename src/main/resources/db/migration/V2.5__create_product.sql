CREATE TABLE product (
    id BIGINT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id),
    name VARCHAR(255) NOT NULL,
    cost_price INT NOT NULL,
    discount_rate INT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES category(id),
    content TEXT,
    average_rating DOUBLE PRECISION DEFAULT 0,
    review_count INT DEFAULT 0,
    thumbnail_img TEXT,
    like_count INT DEFAULT 0,
    sales_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_brand_id ON product(brand_id);
CREATE INDEX idx_product_category_id ON product(category_id);
