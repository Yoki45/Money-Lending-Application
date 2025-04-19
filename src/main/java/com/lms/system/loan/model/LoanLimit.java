package com.lms.system.loan.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.user.model.User;
import com.lms.system.loan.enums.LoanRiskCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="loan_limits")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_loan_amount")
    private Double  limit  = 0d;

    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private User customer;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false)
    private LoanRiskCategory category;

}
