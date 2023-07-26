CREATE TABLE users (
    id                      uuid DEFAULT gen_random_uuid(),
    username                text UNIQUE NOT NULL,
    password_hash           text,
    creation_timestamp      timestamptz,
    PRIMARY KEY (id)
);
