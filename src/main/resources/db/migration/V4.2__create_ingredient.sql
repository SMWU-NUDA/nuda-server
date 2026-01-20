CREATE SEQUENCE ingredient_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_seq'),
    name VARCHAR(150) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
