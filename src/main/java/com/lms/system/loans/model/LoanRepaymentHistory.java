package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.loans.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name= "loan_repayment_histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRepaymentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan", referencedColumnName = "id", nullable = false)
    private Loan loan;

    @Column(name = "amount")
    private Double  amount = 0d;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private LoanStatus status;

    @Column(name = "repaid_on_time")
    private Boolean repaidOnTime;

}
