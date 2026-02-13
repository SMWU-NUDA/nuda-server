CREATE SEQUENCE rec_train_pair_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE rec_train_pair (
    id BIGINT PRIMARY KEY DEFAULT nextval('rec_train_pair_seq'),
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    external_product_id TEXT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT fk_train_event
        FOREIGN KEY (event_id) REFERENCES rec_event_log(id),
    product_feature_id BIGINT NOT NULL,
    CONSTRAINT fk_train_feature
        FOREIGN KEY (product_feature_id) REFERENCES rec_product_feature(id),
    p_sensitivity_level INT,
    p_scent_level INT,
    p_absorbency_level INT,
    p_adhesion_level INT,
    p_safety_level INT,
    d_sensitivity INT,
    d_scent INT,
    d_absorbency INT,
    d_adhesion INT,
    d_safety INT,
    as_of TIMESTAMP NOT NULL
);
