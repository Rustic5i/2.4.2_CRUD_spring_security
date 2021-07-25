
insert into roles (authority) values ('ROLE_ADMIN'),('ROLE_USER');
insert into users (age,email,password,username) values (25,'admin@email.ru','123','admin');
insert into users (age,email,password,username) values (28,'admin1@email.ru','123','admin2');
INSERT INTO `users_role` (`user_id`, `role_id`) VALUES ('1', '1');
INSERT INTO `users_role` (`user_id`, `role_id`) VALUES ('2', '2');