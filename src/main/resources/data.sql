INSERT INTO authority (authority_name) VALUES ('ROLE_USER');
INSERT INTO authority (authority_name) VALUES ('ROLE_ADMIN');
INSERT INTO `users` (`user_id`, `created_at`, `modified_at`, `email`, `name`, `nick_name`, `password`, `phone_number`, `profile_image_url`) VALUES (1, '2023-02-19 15:17:21.840783', '2023-02-19 15:17:21.840783', 'hwsa1004@gmail.com', '천현우', 'kevin', '$2a$10$rbEIIvT0hobOUF2gb.eDV.Iv4UTOS8XFf.m2X152JUvbWyl1UtLym', '010-4345-4377', NULL);
INSERT INTO `user_authority` (`user_id`, `authority_name`) VALUES (1, 'ROLE_USER');
INSERT INTO `groups` (`group_id`, `created_at`, `modified_at`, `group_image_url`, `group_name`, `group_note`,`deleted`) VALUES (1, '2023-02-19 15:17:40.185957', '2023-02-19 15:17:40.185957', NULL, '테스트 그룹 1', '테스트 그룹 1 소개입니당', FALSE);
