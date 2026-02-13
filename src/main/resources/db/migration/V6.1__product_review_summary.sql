CREATE SEQUENCE product_review_summary_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE product_review_summary (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_review_summary_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_review_summary_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    external_product_id TEXT NOT NULL,
    pos_ratio DOUBLE PRECISION,
    neg_ratio DOUBLE PRECISION,
    pos_keyword JSONB,
    neg_keyword JSONB,
    pos_keyword_num INT,
    neg_keyword_num INT
);

CREATE INDEX idx_review_summary_product ON product_review_summary(product_id);
