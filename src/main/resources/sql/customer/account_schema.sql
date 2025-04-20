CREATE TABLE `accounts`
(
    `account_number` bigint NOT NULL,
    `created_on`     datetime(6) NOT NULL,
    `updated_on`     datetime(6) DEFAULT NULL,
    `account_type`   enum('CHECKING','FIX_DEPOSIT','LOAN','SALARY','SAVINGS','STUDENT','TRANSACTION') DEFAULT NULL,
    `active_status`  enum('ACTIVE','INACTIVE') NOT NULL,
    `balance` double DEFAULT NULL,
    `branch_address` varchar(255) DEFAULT NULL,
    `deposits` double DEFAULT NULL,
    `withdrawals` double DEFAULT NULL,
    `created_by`     bigint       DEFAULT NULL,
    `updated_by`     bigint       DEFAULT NULL,
    `customer`       bigint NOT NULL,
    PRIMARY KEY (`account_number`),
    UNIQUE KEY `UK3j1gb647unppxym08c9vtipjc` (`customer`),
    KEY              `FK5sgyirkr3gauvf052hm7x5jix` (`created_by`),
    KEY              `FK4oyasjxav6awna038o7wdv852` (`updated_by`),
    CONSTRAINT `FK4oyasjxav6awna038o7wdv852` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FK5sgyirkr3gauvf052hm7x5jix` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
    CONSTRAINT `FKopr9wppf4a6opovp3xrditkwf` FOREIGN KEY (`customer`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
