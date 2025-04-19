package com.lms.system.loan.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.LoanType;
import com.lms.system.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="loans")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "account", referencedColumnName = "account_number", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    @Column(name = "amount")
    private Double  amount = 0d;

    @Column(name = "balance")
    private Double balance = 0d;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "repaid_date")
    private Date repaidDate;

    @Column(name = "extension_date")
    private Date extensionDate;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private LoanStatus status;

    @JoinColumn(name = "product", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;

    @Enumerated(EnumType.STRING)
    private LoanType loanType = LoanType.DEFAULT;

}
