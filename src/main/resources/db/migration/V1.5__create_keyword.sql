CREATE SEQUENCE keyword_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE keyword (
    id BIGINT PRIMARY KEY DEFAULT nextval('keyword_seq'),
    member_id BIGINT NOT NULL,
    irritation_level VARCHAR(20),
    scent VARCHAR(20),
    change_frequency VARCHAR(20),
    thickness VARCHAR(20),
    priority VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_keyword_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE INDEX idx_keyword_member_id
    ON keyword(member_id);
