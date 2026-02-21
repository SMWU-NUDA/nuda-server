CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS ml_product_embedding (
    product_id BIGINT PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    embedding  vector(768) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ml_product_embedding_hnsw_idx
    ON ml_product_embedding
    USING hnsw (embedding vector_cosine_ops);
