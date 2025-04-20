insert into lending_application.loan_installments (id, created_on, updated_on, amount, balance, due_date,
                                                   extension_date, installment_number, payment_status, loan_status,
                                                   created_by, updated_by, loan)
values (1, '2025-04-20 13:13:34.478000', '2025-04-20 13:22:34.996000', 250, 50, '2025-06-01 00:00:00.000000', null, 1,
        'NOT_PAID', 'OPEN', null, 1, 1),
       (2, '2025-04-20 13:13:34.485000', '2025-04-20 13:22:34.996000', 250, 250, '2025-07-01 00:00:00.000000', null, 2,
        'NOT_PAID', 'OPEN', null, 1, 1),
       (3, '2025-04-20 13:14:54.695000', '2025-04-20 13:18:42.144000', 300, 0, '2025-05-20 00:00:00.000000', null, 1,
        'PAID', 'CLOSED', null, 1, 2),
       (4, '2025-04-20 13:14:54.695000', '2025-04-20 13:19:14.254000', 300, 0, '2025-06-19 00:00:00.000000', null, 2,
        'PAID', 'CLOSED', null, 1, 2);