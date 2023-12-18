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

CREATE TABLE `status`  
(
    id INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) COLLATE utf8_unicode_ci NOT NULL,
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE 'task'
(
    id INT(11) NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) COLLATE utf8_unicode_ci NOT NULL,
    detail VARCHAR(500) COLLATE utf8_unicode_ci NOT NULL,
    deadline DATETIME COLLATE utf8_unicode_ci NOT NULL,
    status_id INT(11) NOT NULL,
    `user_id` INT(11) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_task_status FOREIGN KEY (status_id) REFERENCES `status`(id),
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES `users`(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `task-status`
(
    id INT(11) NOT NULL AUTO_INCREMENT,
    task_id INT(11) COLLATE utf8_unicode_ci NOT NULL,
    status_id INT(11) COLLATE utf8_unicode_ci NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_task_status_task FOREIGN KEY (task_id) REFERENCES `task`(id),
    CONSTRAINT fk_task_status_status FOREIGN KEY (status_id) REFERENCES `status`(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


INSERT INTO `users` (`name`, email, `password`, phonenumber, address, job, homepage) VALUES
('Hoai Bao', 'hoaibao@gmail.com', 'hoaibao123','9876543210', 'Vietnam', 'Backend Developer', 'hoaibaotran.info'),
('Anh Khoa', 'anhkhoa@gmail.com', 'anhkhoa123', '1234567899', 'Vietnam', 'Frontend Developer', 'anhkhoaphan.info'),
('Luffy', 'luffy@gmail.com', 'luffy123', '0897869823', 'East Blue', 'Pirates', 'luffymonkey.info'),
('Gojo Satoru', 'satorugojo@gmail.com', 'satoru123', '0789456321', 'Tokyo', 'Jujutsu Sorcerer', 'satorugojo.info'),
('Ichigo', 'ichigo@gmail.com', 'ichigo123', '0975557431', 'Hollow Town', 'Shinigami', 'ichigo.info')

INSERT INTO `status` (`name`) VALUES 
('Ready'), 
('In progress'), 
('Done');

INSERT INTO `task` (title, detail, deadline, status_id, `user_id`) VALUES
('Task 1', 'Details for Task 1', '2023-12-31 12:00:00', 1, 1),
('Task 2', 'Details for Task 2', '2023-12-31 14:30:00', 2, 2),
('Task 3', 'Details for Task 3', '2023-12-31 16:45:00', 1, 3),
('Task 4', 'Details for Task 4', '2023-12-31 18:15:00', 3, 1),
('Task 5', 'Details for Task 5', '2023-12-31 20:00:00', 2, 2);

INSERT INTO `task-status` (task_id, status_id) VALUES 
(1, 2),
(3, 1),
(4, 3),
(5, 2),
(2, 3);