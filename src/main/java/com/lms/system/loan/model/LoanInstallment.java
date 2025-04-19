package com.lms.system.loan.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="loan_installments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanInstallment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "loan", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "extension_date")
    private Date extensionDate;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status")
    private LoanStatus status;

}
