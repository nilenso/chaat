CREATE TABLE users (
    id                      uuid DEFAULT gen_random_uuid(),
    username                text UNIQUE NOT NULL,
    dynamic_salt            text,
    password_hash           text,
    creation_timestamp      timestamptz,
    display_picture         text,
    PRIMARY KEY (id)
);
