CREATE SEQUENCE msds_candidate_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE msds_candidate (
    id BIGINT PRIMARY KEY DEFAULT nextval('msds_candidate_seq'),
    ingredient_normalized_id BIGINT NOT NULL,
    CONSTRAINT fk_candidate_ingredient
        FOREIGN KEY (ingredient_normalized_id) REFERENCES ingredient_normalized(id) ON DELETE CASCADE,
    source TEXT,
    candidate_rank INT,
    chemical_name TEXT,
    cas_no TEXT,
    msds_id TEXT,
    confidence DOUBLE PRECISION,
    raw_payload XML
);

CREATE INDEX idx_msds_candidate_ing ON msds_candidate(ingredient_normalized_id);
