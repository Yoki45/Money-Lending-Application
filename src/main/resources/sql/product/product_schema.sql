CREATE TABLE `products` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_on` datetime(6) NOT NULL,
                            `updated_on` datetime(6) DEFAULT NULL,
                            `active_status` enum('ACTIVE','INACTIVE') NOT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `tenure_type` enum('DAYS','MONTHS') DEFAULT NULL,
                            `tenure_value` int DEFAULT NULL,
                            `created_by` bigint DEFAULT NULL,
                            `updated_by` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKl0lce8i162ldn9n01t2a6lcix` (`created_by`),
                            KEY `FKdeswm6d74skv6do803axl6edj` (`updated_by`),
                            CONSTRAINT `FKdeswm6d74skv6do803axl6edj` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
                            CONSTRAINT `FKl0lce8i162ldn9n01t2a6lcix` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
