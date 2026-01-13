CREATE TABLE member (
    id BIGINT PRIMARY KEY DEFAULT nextval('member_seq'),
    nickname VARCHAR(50),
    username VARCHAR(50) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_img TEXT,
    role VARCHAR(20) NOT NULL,
    signup_step VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    postal_code VARCHAR(20),
    address1 VARCHAR(255),
    address2 VARCHAR(255),
    phone_num VARCHAR(30),
    recipient VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
