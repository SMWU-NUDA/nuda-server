CREATE SEQUENCE product_ai_summary_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE product_ai_summary (
    id BIGINT PRIMARY KEY DEFAULT nextval('product_ai_summary_seq'),
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
