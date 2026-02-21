ALTER TABLE keyword
    DROP COLUMN change_frequency,
    DROP COLUMN priority;

ALTER TABLE keyword
    ADD COLUMN adhesion VARCHAR(20);
