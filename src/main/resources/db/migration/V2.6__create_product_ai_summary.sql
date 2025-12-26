CREATE TABLE product_ai_summary (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    satisfaction_rate DOUBLE PRECISION,
    positive_keywords JSONB,
    negative_keywords JSONB,
    trend_summary TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_ai_summary_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_product_ai_summary_updated_at
    BEFORE UPDATE ON product_ai_summary
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();