CREATE TABLE signup_draft (
    id BIGINT PRIMARY KEY DEFAULT nextval('signup_draft_seq'),
    signup_token VARCHAR(64) NOT NULL UNIQUE,
    current_step VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_signup_draft_token
    ON signup_draft (signup_token);