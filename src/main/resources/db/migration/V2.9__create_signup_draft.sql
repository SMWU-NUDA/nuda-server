CREATE TABLE signup_draft (
    id BIGINT PRIMARY KEY DEFAULT nextval('signup_draft_seq'),
    signup_token VARCHAR(64) NOT NULL UNIQUE,
    current_step VARCHAR(30) NOT NULL,

    email VARCHAR(255),
    username VARCHAR(50),
    password VARCHAR(255),
    nickname VARCHAR(50),

    postal_code VARCHAR(20),
    address1 VARCHAR(255),
    address2 VARCHAR(255),
    phone_num VARCHAR(30),
    recipient VARCHAR(50),

    irritation_level VARCHAR(20),
    scent VARCHAR(20),
    change_frequency VARCHAR(20),
    thickness VARCHAR(20),
    priority VARCHAR(20),
    product_ids TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
