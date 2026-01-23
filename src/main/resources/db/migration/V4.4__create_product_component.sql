CREATE SEQUENCE product_component_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE product_component (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_component_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_product_component_product
        FOREIGN KEY (product_id) REFERENCES product (id),
    component_id BIGINT NOT NULL,
    CONSTRAINT fk_product_component_component
        FOREIGN KEY (component_id) REFERENCES component (id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_product_component
        UNIQUE (product_id, component_id)
);

CREATE INDEX idx_product_component_component ON product_component(component_id);
