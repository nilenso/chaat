CREATE TABLE users (
    id                      uuid DEFAULT gen_random_uuid(),
    from                    uuid,
    to                      uuid,
    status                  text,
    message                 text,
    sent_timestamp          timestamptz,
    PRIMARY KEY (id)
);
