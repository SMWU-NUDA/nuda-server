CREATE TABLE review (
    id BIGINT PRIMARY KEY DEFAULT nextval('review_seq'),
    member_id BIGINT NOT NULL,
    CONSTRAINT fk_review_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_review_product
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    pros TEXT,
    cons TEXT,
    rating DOUBLE PRECISION NOT NULL CHECK (rating >= 0 AND rating <= 5),
    like_count INT NOT NULL DEFAULT 0,
    thumbnail_img TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_review_product_id ON review(product_id);
CREATE INDEX idx_review_member_id ON review(member_id);
