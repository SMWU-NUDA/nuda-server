CREATE SEQUENCE ai_review_summary_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ai_review_summary (
    id BIGINT PRIMARY KEY DEFAULT nextval('ai_review_summary_seq'),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_ai_review_summary_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    external_product_id TEXT NOT NULL,
    summary TEXT,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_ai_review_summary_product ON ai_review_summary(product_id);
