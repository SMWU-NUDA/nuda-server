CREATE TABLE IF NOT EXISTS review_feature_score (
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_rfs_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL,
    CONSTRAINT fk_rfs_review
        FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE,
    feature_key TEXT   NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    matched JSONB,
    review_updated_at TIMESTAMPTZ NOT NULL,
    computed_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (product_id, review_id, feature_key)
);

CREATE INDEX IF NOT EXISTS idx_rfs_prod_feature_score ON review_feature_score (product_id, feature_key, score DESC, review_id DESC);
CREATE INDEX IF NOT EXISTS idx_rfs_prod_feature_computed ON review_feature_score (product_id, feature_key, computed_at);
CREATE INDEX IF NOT EXISTS idx_rfs_review ON review_feature_score (review_id);
