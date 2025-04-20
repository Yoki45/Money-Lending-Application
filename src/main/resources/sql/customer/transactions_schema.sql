CREATE TABLE `transactions`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `created_on`       datetime(6) NOT NULL,
    `updated_on`       datetime(6) DEFAULT NULL,
    `amount` double DEFAULT NULL,
    `transaction_type` enum('DEPOSIT','WITHDRAWAL') NOT NULL,
    `created_by`       bigint DEFAULT NULL,
    `updated_by`       bigint DEFAULT NULL,
    `account`          bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY                `FKejem3mgn2s0rcputq0m42nx1t` (`created_by`),
    KEY                `FKstk5l76bqu7t80jc0qejb6bv4` (`updated_by`),
    KEY                `FKkf8d41ejdmm82511i82xgrhve` (`account`),
    CONSTRAINT `FKejem3mgn2s0rcputq0m42nx1t` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKkf8d41ejdmm82511i82xgrhve` FOREIGN KEY (`account`) REFERENCES `accounts` (`account_number`),
    CONSTRAINT `FKstk5l76bqu7t80jc0qejb6bv4` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
