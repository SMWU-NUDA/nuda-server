ALTER TABLE signup_draft
DROP COLUMN change_frequency,
    DROP COLUMN priority;

ALTER TABLE signup_draft
    ADD COLUMN adhesion VARCHAR(20);
