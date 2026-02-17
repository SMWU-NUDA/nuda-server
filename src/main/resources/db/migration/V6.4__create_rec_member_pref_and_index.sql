CREATE TABLE IF NOT EXISTS rec_member_pref (
    member_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_rec_member_pref_member
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    p_sensitivity_level INT NOT NULL,
    p_scent_level       INT NOT NULL,
    p_absorbency_level  INT NOT NULL,
    p_adhesion_level    INT NOT NULL,
    p_safety_level      INT NOT NULL,
    as_of TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS rec_train_pair_member_id_ux ON rec_train_pair (member_id);
