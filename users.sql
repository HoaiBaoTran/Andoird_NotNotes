CREATE DATABASE `notnotes`;
USE notnotes;


CREATE TABLE `users`
(
    id INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(40) COLLATE utf8_unicode_ci NOT NULL,
    email VARCHAR(40) COLLATE utf8_unicode_ci NOT NULL UNIQUE,
    `password` VARCHAR(40) COLLATE utf8_unicode_ci NOT NULL,
    phonenumber VARCHAR(40) COLLATE utf8_unicode_ci,
    address VARCHAR(40) COLLATE utf8_unicode_ci,
    job VARCHAR(40) COLLATE utf8_unicode_ci,
    homepage VARCHAR(40) COLLATE utf8_unicode_ci,
    PRIMARY KEY(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `users` (`name`, email, `password`, phonenumber, address, job, homepage) VALUES
('Hoai Bao', 'hoaibao@gmail.com', 'hoaibao123','9876543210', 'Vietnam', 'Backend Developer', 'hoaibaotran.info'),
('Anh Khoa', 'anhkhoa@gmail.com', 'anhkhoa123', '1234567899', 'Vietnam', 'Frontend Developer', 'anhkhoaphan.info'),
('Luffy', 'luffy@gmail.com', 'luffy123', '0897869823', 'East Blue', 'Pirates', 'luffymonkey.info'),
('Gojo Satoru', 'satorugojo@gmail.com', 'satoru123', '0789456321', 'Tokyo', 'Jujutsu Sorcerer', 'satorugojo.info'),
('Ichigo', 'ichigo@gmail.com', 'ichigo123', '0975557431', 'Hollow Town', 'Shinigami', 'ichigo.info')