CREATE SEQUENCE product_review_keywords_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE product_review_keywords (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_review_keywords_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_review_keywords_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    external_product_id TEXT NOT NULL,
    top_keywords JSONB,
    aspect_keywords JSONB,
    source_keyphrases JSONB,
    feature_keywords JSONB,
    feature_scores JSONB
);

CREATE INDEX idx_review_keywords_product ON product_review_keywords(product_id);
