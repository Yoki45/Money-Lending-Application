CREATE TABLE `users`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `communication_chanel` enum('EMAIL','MOBILE','OTHER') DEFAULT NULL,
    `name`                 varchar(255) NOT NULL,
    `password`             varchar(255) NOT NULL,
    `phone`                varchar(255) NOT NULL,
    `username`             varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
