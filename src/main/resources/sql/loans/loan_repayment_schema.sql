CREATE TABLE `loan_repayment_histories` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `created_on` datetime(6) NOT NULL,
                                            `updated_on` datetime(6) DEFAULT NULL,
                                            `amount` double DEFAULT NULL,
                                            `repaid_on_time` bit(1) DEFAULT NULL,
                                            `loan_status` enum('CANCELLED','CLOSED','OPEN','OVERDUE','WRITTEN_OFF') DEFAULT NULL,
                                            `created_by` bigint DEFAULT NULL,
                                            `updated_by` bigint DEFAULT NULL,
                                            `loan` bigint NOT NULL,
                                            PRIMARY KEY (`id`),
                                            KEY `FK7mgd866wxpwicng03h3wo64r3` (`created_by`),
                                            KEY `FKslej0gov9dbdvy513ylqrmu0o` (`updated_by`),
                                            KEY `FK2xaq8fpnjybourccl7skq55v9` (`loan`),
                                            CONSTRAINT `FK2xaq8fpnjybourccl7skq55v9` FOREIGN KEY (`loan`) REFERENCES `loans` (`id`),
                                            CONSTRAINT `FK7mgd866wxpwicng03h3wo64r3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
                                            CONSTRAINT `FKslej0gov9dbdvy513ylqrmu0o` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
