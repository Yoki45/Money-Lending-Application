CREATE TABLE `loan_limits`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `created_on`    datetime(6) NOT NULL,
    `updated_on`    datetime(6) DEFAULT NULL,
    `risk_category` enum('ELEVATED','HIGH','INELIGIBLE','LOW','MEDIUM') NOT NULL,
    `max_loan_amount` double DEFAULT NULL,
    `created_by`    bigint DEFAULT NULL,
    `updated_by`    bigint DEFAULT NULL,
    `customer`      bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK1nmxdt7oh0f4v5mossg1x553j` (`customer`),
    KEY             `FKpbuelclk6e1xpaqlf86gpdits` (`created_by`),
    KEY             `FK813ro8iqvsb5fnip8l89cq9m5` (`updated_by`),
    CONSTRAINT `FK813ro8iqvsb5fnip8l89cq9m5` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKl5pjflqkh8qwvsdf4fycred1x` FOREIGN KEY (`customer`) REFERENCES `users` (`id`),
    CONSTRAINT `FKpbuelclk6e1xpaqlf86gpdits` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
