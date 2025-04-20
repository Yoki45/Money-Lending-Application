CREATE TABLE `credit_score_histories`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `created_on`   datetime(6) NOT NULL,
    `updated_on`   datetime(6) DEFAULT NULL,
    `score_value` double DEFAULT NULL,
    `created_by`   bigint DEFAULT NULL,
    `updated_by`   bigint DEFAULT NULL,
    `credit_score` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKj7dxkb578inh1mgkb4cdxg0lh` (`created_by`),
    KEY            `FKfr8sfqqf4j1mjf4o9dt4wf4ej` (`updated_by`),
    KEY            `FKfakk3eg9ckm44503sfsyfpe36` (`credit_score`),
    CONSTRAINT `FKfakk3eg9ckm44503sfsyfpe36` FOREIGN KEY (`credit_score`) REFERENCES `credit_scores` (`id`),
    CONSTRAINT `FKfr8sfqqf4j1mjf4o9dt4wf4ej` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKj7dxkb578inh1mgkb4cdxg0lh` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
