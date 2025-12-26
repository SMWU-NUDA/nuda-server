CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE product_embedding (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    embedding_vector VECTOR(1536),
    summary_text TEXT,
    version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_embedding_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_product_embedding_updated_at
    BEFORE UPDATE ON product_embedding
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();