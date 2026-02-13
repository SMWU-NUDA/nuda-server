CREATE SEQUENCE rec_product_feature_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE rec_product_feature (
    id BIGINT PRIMARY KEY DEFAULT nextval('rec_product_feature_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_rec_product_feature_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    external_product_id TEXT NOT NULL,
    sensitivity_level INT NOT NULL,
    scent_level INT NOT NULL,
    absorbency_level INT NOT NULL,
    adhesion_level INT NOT NULL,
    safety_level INT NOT NULL
);

CREATE INDEX idx_rec_product_feature_product ON rec_product_feature(product_id);
