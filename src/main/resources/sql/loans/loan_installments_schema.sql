CREATE TABLE `loan_installments`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_on`         datetime(6) NOT NULL,
    `updated_on`         datetime(6) DEFAULT NULL,
    `amount` double NOT NULL,
    `balance` double NOT NULL,
    `due_date`           datetime(6) NOT NULL,
    `extension_date`     datetime(6) DEFAULT NULL,
    `installment_number` int    NOT NULL,
    `payment_status`     enum('NOT_PAID','PAID') DEFAULT NULL,
    `loan_status`        enum('CANCELLED','CLOSED','OPEN','OVERDUE','WRITTEN_OFF') DEFAULT NULL,
    `created_by`         bigint DEFAULT NULL,
    `updated_by`         bigint DEFAULT NULL,
    `loan`               bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY                  `FK8qwjft6qcf2yuimkmkr47sfgx` (`created_by`),
    KEY                  `FKb5fjanvagh1gwthbrg09clr6j` (`updated_by`),
    KEY                  `FK283lx56ne4onacj3wiitgktko` (`loan`),
    CONSTRAINT `FK283lx56ne4onacj3wiitgktko` FOREIGN KEY (`loan`) REFERENCES `loans` (`id`),
    CONSTRAINT `FK8qwjft6qcf2yuimkmkr47sfgx` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKb5fjanvagh1gwthbrg09clr6j` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
