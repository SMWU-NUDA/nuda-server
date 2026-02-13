CREATE TYPE hazard_level AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'UNKNOWN'
);

CREATE SEQUENCE msds_hazard_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE msds_hazard (
    id BIGINT PRIMARY KEY DEFAULT nextval('msds_hazard_seq'),
    candidate_id BIGINT NOT NULL,
    CONSTRAINT fk_hazard_candidate
        FOREIGN KEY (candidate_id) REFERENCES msds_candidate(id) ON DELETE CASCADE,
    signal_word TEXT,
    h_codes JSONB,
    risk_score INT,
    Field hazard_level,
    confidence DOUBLE PRECISION,
    unknown_reason TEXT,
    risk_basis JSONB,
    extracted JSONB
);

CREATE INDEX idx_msds_hazard_candidate ON msds_hazard(candidate_id);
