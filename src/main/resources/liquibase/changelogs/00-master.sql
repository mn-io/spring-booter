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

ALTER TABLE user
    ADD PRIMARY KEY (id);

ALTER TABLE user
    ADD CONSTRAINT UC_USER_EMAIL_COL UNIQUE (email);

CREATE TABLE user_session
(
    id            VARCHAR(255)  NOT NULL,
    created       datetime      NULL,
    last_modified datetime      NULL,
    version       INT DEFAULT 0 NOT NULL,
    token         VARCHAR(255)  NOT NULL,
    user_id       VARCHAR(255)  NOT NULL
);

ALTER TABLE user_session
    ADD PRIMARY KEY (id);

ALTER TABLE user_session
    ADD CONSTRAINT UC_USER_SESSION_TOKEN_COL UNIQUE (token);

ALTER TABLE user_session
    ADD CONSTRAINT UKfc0y1f6pxscc6hn7ajw4xl9n6 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE;
