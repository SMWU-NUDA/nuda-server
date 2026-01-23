CREATE SEQUENCE product_ingredient_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE product_ingredient (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_ingredient_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_product_ingredient_product
        FOREIGN KEY (product_id) REFERENCES product (id),
    component_id BIGINT NOT NULL,
    CONSTRAINT fk_product_ingredient_component
        FOREIGN KEY (component_id) REFERENCES component (id),
    ingredient_id BIGINT NOT NULL,
    CONSTRAINT fk_product_ingredient_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_product_component_ingredient
        UNIQUE (product_id, component_id, ingredient_id)
);

CREATE INDEX idx_product_ingredient_product ON product_ingredient(product_id);
CREATE INDEX idx_product_ingredient_ingredient ON product_ingredient(ingredient_id);
CREATE INDEX idx_product_ingredient_product_component ON product_ingredient(product_id, component_id);
