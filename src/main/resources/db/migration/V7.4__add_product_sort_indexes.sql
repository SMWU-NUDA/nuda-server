CREATE INDEX idx_product_review_desc_id_desc ON product (review_count DESC, id DESC);
CREATE INDEX idx_product_rating_desc_id_desc ON product (average_rating DESC, id DESC);
