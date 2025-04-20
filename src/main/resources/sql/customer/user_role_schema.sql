CREATE TABLE `user_roles`
(
    `id`   bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    `user` bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY    `FK5klgup8egcm3o93sfutticc09` (`user`),
    CONSTRAINT `FK5klgup8egcm3o93sfutticc09` FOREIGN KEY (`user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
