CREATE TABLE survey (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    irritation_level irritation_level,
    scent scent_level,
    change_frequency change_frequency,
    thickness thickness_level,
    priority priority_type,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_survey_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_survey_updated_at
    BEFORE UPDATE ON survey
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_survey_member_id
    ON survey(member_id);