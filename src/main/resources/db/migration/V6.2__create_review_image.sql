CREATE TABLE review_image (
    id BIGINT PRIMARY KEY DEFAULT nextval('review_image_seq'),
    review_id BIGINT NOT NULL,
    CONSTRAINT fk_review_image_review
        FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    sequence INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_review_image_sequence UNIQUE (review_id, sequence)
);

CREATE INDEX idx_review_image_review_id ON review_image(review_id);
