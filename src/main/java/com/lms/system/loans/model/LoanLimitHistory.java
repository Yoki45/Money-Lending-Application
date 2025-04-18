package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.loans.enums.LoanRiskCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Table(name="loan_limit_histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LoanLimitHistory  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "loan_limit", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LoanLimit loanLimit;

    @Column(name = "amount")
    private Double  limit = 0d;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false)
    private LoanRiskCategory category;
}
