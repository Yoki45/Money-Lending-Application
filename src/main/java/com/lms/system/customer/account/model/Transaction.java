package com.lms.system.customer.account.model;


import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.account.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "account", referencedColumnName = "account_number", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    @Column(name = "amount")
    private Double amount;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

}
