ALTER TABLE signup_draft
    DROP COLUMN priority;

ALTER TABLE signup_draft
    ADD COLUMN adhesion VARCHAR(20);
