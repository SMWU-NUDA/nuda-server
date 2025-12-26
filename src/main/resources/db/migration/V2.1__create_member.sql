CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(50),
    username VARCHAR(50) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_img TEXT,
    role member_role NOT NULL,
    status member_status NOT NULL,
    postal_code VARCHAR(20),
    address1 VARCHAR(255),
    address2 VARCHAR(255),
    phone_num VARCHAR(30),
    recipient VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_member_updated_at
    BEFORE UPDATE ON member
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
