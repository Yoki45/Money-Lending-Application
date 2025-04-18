package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Table(name="loan_repayment_histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LoanLimitHistory  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "loan_limit", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private LoanLimit loanLimit;

    @Column(name = "limit")
    private Double  limit = 0d;
}
