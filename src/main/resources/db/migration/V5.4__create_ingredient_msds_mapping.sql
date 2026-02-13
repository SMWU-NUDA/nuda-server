CREATE SEQUENCE ingredient_msds_mapping_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient_msds_mapping (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_msds_mapping_seq'),
    ingredient_normalized_id BIGINT NOT NULL,
    CONSTRAINT fk_mapping_ingredient
        FOREIGN KEY (ingredient_normalized_id) REFERENCES ingredient_normalized(id) ON DELETE CASCADE,
    chosen_candidate_id BIGINT NOT NULL,
    CONSTRAINT fk_mapping_candidate
        FOREIGN KEY (chosen_candidate_id) REFERENCES msds_candidate(id) ON DELETE SET NULL,
    match_status TEXT,
    match_confidence DOUBLE PRECISION,
    decided_by TEXT
);

CREATE INDEX idx_mapping_ing ON ingredient_msds_mapping(ingredient_normalized_id);
