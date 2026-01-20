CREATE SEQUENCE ingredient_risk_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient_risk (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_risk_seq'),
    ingredient_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_risk_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id),
    source VARCHAR(20) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    toxicity VARCHAR(255),
    reference_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ingredient_risk_ingredient ON ingredient_risk(ingredient_id);
CREATE INDEX idx_ingredient_risk_ingredient_source ON ingredient_risk(ingredient_id, source);
