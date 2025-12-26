CREATE TABLE member_vector (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL UNIQUE,
    preference_vector VECTOR(1536),
    version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_member_vector_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_member_vector_updated_at
    BEFORE UPDATE ON member_vector
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();