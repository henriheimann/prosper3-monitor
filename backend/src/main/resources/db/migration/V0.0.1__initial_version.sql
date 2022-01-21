CREATE TABLE devices
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(256) NOT NULL,
    ttn_id  VARCHAR(256)
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
