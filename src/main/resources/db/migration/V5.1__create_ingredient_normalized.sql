CREATE SEQUENCE ingredient_normalized_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient_normalized (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_normalized_seq'),
    ingredient_id BIGINT NOT NULL,
    raw_name TEXT NOT NULL,
    normalized_name TEXT NOT NULL
);

CREATE INDEX idx_ingredient_normalized_ing ON ingredient_normalized(ingredient_id);
