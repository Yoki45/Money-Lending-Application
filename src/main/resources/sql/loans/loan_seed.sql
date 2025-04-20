insert into lending_application.loans (id, created_on, updated_on, amount, balance, due_date, extension_date, loan_type,
                                       repaid_date, loan_status, created_by, updated_by, account, product)
values (1, '2025-04-20 13:13:34.468000', '2025-04-20 13:22:34.996000', 400, 300, '2025-07-01 00:00:00.000000', null,
        'CONSOLIDATED', null, 'OPEN', null, 1, 1039245257, 1),
       (2, '2025-04-20 13:14:54.692000', '2025-04-20 13:19:14.257000', 500, 0, '2025-05-20 00:00:00.000000', null,
        'DEFAULT', '2025-04-20 13:19:14.257000', 'CLOSED', null, 1, 1039245257, 1),
       (3, '2025-04-20 13:15:31.007000', '2025-04-20 13:22:34.996000', 500, 600, '2025-06-01 00:00:00.000000', null,
        'CONSOLIDATED', null, 'OPEN', null, 1, 1039245257, 1);