CREATE TABLE friend_requests (
    id                      SERIAL,
    sender_username         text,
    recipient_username      text,
    request_state           text,
    msg                     text,
    creation_timestamp      timestamptz,
    PRIMARY KEY (id)    
);
