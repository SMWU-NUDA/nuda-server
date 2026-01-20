CREATE SEQUENCE member_vector_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE member_vector (
    id BIGINT PRIMARY KEY DEFAULT nextval('member_vector_seq'),
    member_id BIGINT NOT NULL UNIQUE,
    preference_vector VECTOR(1536),
    version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_member_vector_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);
