-- liquibase formatted sql

-- changeset mn-io:0-1
CREATE TABLE user
(
    id            VARCHAR(255)  NOT NULL,
    created       datetime      NULL,
    last_modified datetime      NULL,
    version       INT DEFAULT 0 NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    name          VARCHAR(255)  NULL,
    password      VARCHAR(255)  NOT NULL
);

CREATE TABLE user_session
(
    id            VARCHAR(255)  NOT NULL,
    created       datetime      NULL,
    last_modified datetime      NULL,
    version       INT DEFAULT 0 NOT NULL,
    token         VARCHAR(255)  NOT NULL,
    user_id       VARCHAR(255)  NULL
);

ALTER TABLE user
    ADD CONSTRAINT UC_USER_EMAIL_COL UNIQUE (email);

ALTER TABLE user_session
    ADD CONSTRAINT UC_USER_SESSION_TOKEN_COL UNIQUE (token);

