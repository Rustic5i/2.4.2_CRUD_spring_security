
insert into roles (authority) values ('ROLE_ADMIN'),('ROLE_USER');
insert into users (age,email,password,username) values (25,'admin@email.ru','admin','admin');
INSERT INTO `users_role` (`user_id`, `role_id`) VALUES ('1', '1');