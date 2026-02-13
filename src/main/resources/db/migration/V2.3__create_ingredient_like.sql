CREATE SEQUENCE ingredient_like_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ingredient_like (
    id BIGINT PRIMARY KEY DEFAULT nextval('ingredient_like_seq'),
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_like_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_like_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id) ON DELETE CASCADE,
    preference BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_member_ingredient_like
        UNIQUE (member_id, ingredient_id)
);

CREATE INDEX idx_ingredient_like_member_ingredient ON ingredient_like(member_id, ingredient_id);
