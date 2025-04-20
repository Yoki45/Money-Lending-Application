CREATE TABLE `credit_scores`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_on` datetime(6) NOT NULL,
    `updated_on` datetime(6) DEFAULT NULL,
    `score` double DEFAULT NULL,
    `created_by` bigint DEFAULT NULL,
    `updated_by` bigint DEFAULT NULL,
    `customer`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKjd2pcuho5q1ob10he0hijei2l` (`customer`),
    KEY          `FKfklwokehgi92du0d6osk2alyy` (`created_by`),
    KEY          `FK6ga8jvru5jkgyldx5pa3uy6hh` (`updated_by`),
    CONSTRAINT `FK6ga8jvru5jkgyldx5pa3uy6hh` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKfklwokehgi92du0d6osk2alyy` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKor6acv853ue8kbs95qo3gdcmp` FOREIGN KEY (`customer`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
