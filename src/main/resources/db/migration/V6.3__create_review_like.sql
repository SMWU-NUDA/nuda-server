CREATE TABLE review_like (
    id BIGINT PRIMARY KEY DEFAULT nextval('review_like_seq'),
    review_id BIGINT NOT NULL,
    CONSTRAINT fk_review_like_review
        FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE,
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_review_like_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_review_like_unique UNIQUE (review_id, member_id)
);
