CREATE SEQUENCE component_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE component (
    id BIGINT PRIMARY KEY DEFAULT nextval('component_seq'),
    name VARCHAR(100) NOT NULL,
    content TEXT,
    layer_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
