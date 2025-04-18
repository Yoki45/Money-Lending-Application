package com.lms.system.product.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.product.enums.FeeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "product_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "is_percentage", nullable = false)
    private Boolean isPercentage;

    @Column(name = "apply_on_disbursement", nullable = false)
    private Boolean applyOnDisbursement;

    @Column(name = "trigger_days")
    private Integer triggerDaysAfterDue;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "active_status", nullable = false)
    private ActiveStatus activeStatus;

}
