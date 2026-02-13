CREATE SEQUENCE ingredient_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_seq'),
    name VARCHAR(150) NOT NULL,
    risk_level VARCHAR(20) NOT NULL
        CHECK (risk_level IN ('SAFE','WARN','DANGER','UNKNOWN')),
    layer_type VARCHAR(20)
        CHECK (layer_type IN ('TOP_SHEET','ABSORBER','BACK_SHEET','ADDITIVE','ADHESIVE')),
    content TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_ingredient_name_layer UNIQUE (name, layer_type)
);

CREATE INDEX idx_ingredient_name ON ingredient(name);
