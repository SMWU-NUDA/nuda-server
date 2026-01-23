CREATE SEQUENCE survey_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE survey (
    id BIGINT PRIMARY KEY DEFAULT nextval('survey_seq'),
    member_id BIGINT NOT NULL,
    irritation_level VARCHAR(20),
    scent VARCHAR(20),
    change_frequency VARCHAR(20),
    thickness VARCHAR(20),
    priority VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_survey_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE INDEX idx_survey_member_id
    ON survey(member_id);
