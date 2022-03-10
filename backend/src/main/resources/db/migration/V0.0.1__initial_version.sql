CREATE TABLE devices
(
    id SERIAL PRIMARY KEY,
    qr_code_id INTEGER UNIQUE,
    name VARCHAR(256) NOT NULL,
    ttn_id VARCHAR(256),
    ttn_device_address VARCHAR(256),
    ttn_network_session_key VARCHAR(256),
    ttn_application_session_key VARCHAR(256),
    latitude DOUBLE PRECISION NOT NULL DEFAULT 0.00,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 0.00
);

CREATE TYPE user_role_enum AS ENUM ('USER', 'ADMIN');

CREATE TABLE users
(
    username VARCHAR(256) PRIMARY KEY,
    password VARCHAR(256) NOT NULL,
    role VARCHAR(256) NOT NULL
);

CREATE TABLE tokens
(
    token VARCHAR(256) PRIMARY KEY,
    owner VARCHAR(256) NOT NULL,
    expiry TIMESTAMP NOT NULL,

    FOREIGN KEY (owner) REFERENCES users(username)
);
