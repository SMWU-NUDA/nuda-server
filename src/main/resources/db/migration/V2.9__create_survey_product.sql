CREATE TABLE survey_product (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_survey_product_survey
        FOREIGN KEY (survey_id) REFERENCES survey(id) ON DELETE CASCADE,
    CONSTRAINT fk_survey_product_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT uq_survey_product UNIQUE (survey_id, product_id)
);

CREATE INDEX idx_survey_product_survey_id
    ON survey_product(survey_id);
CREATE INDEX idx_survey_product_product_id
    ON survey_product(product_id);
