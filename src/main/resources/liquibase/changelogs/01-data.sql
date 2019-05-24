-- liquibase formatted sql

-- changeset mn-io:0-1
INSERT INTO `user` (`id`, `created`, `last_modified`, `version`, `email`, `name`, `password`)
VALUES ('00000000-0000-0000-0000-000000000001', NOW(), NOW(), 0, 'alice@alice.com', 'Alice',
        '$shiro1$SHA-512$50000$TmhlYXNHa0kwSA==$AIUxG5t8oVIxEEokXEYyHW0EIcysMORlOfLqtk3sdViQlDA9ra2y19kV7zWtUdIWYMIQc/aRGLYDHMMLKCmadQ==');
-- hashed password: alice

INSERT INTO `user_session` (`id`, `created`, `last_modified`, `version`, `token`, `user_id`)
VALUES ('00000000-0000-0000-0000-000000000002', NOW(), NOW(), 0, 'alice-session-token',
        '00000000-0000-0000-0000-000000000001');
