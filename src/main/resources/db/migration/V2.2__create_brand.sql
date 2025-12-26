CREATE TABLE brand (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_img TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_brand_updated_at
BEFORE UPDATE ON brand
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
