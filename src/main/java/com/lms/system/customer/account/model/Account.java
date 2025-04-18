package com.lms.system.customer.account.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.customer.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Column(name = "account_number")
    @Id
    private Long accountNumber;

    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private User customer;

    @Column(name = "account_type")
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "branch_address")
    private String branchAddress;

    @Column(name = "balance")
    private Double balance = 0d;
    @Column(name = "deposits")
    private Double deposits = 0d;
    @Column(name = "withdrawals")
    private Double withdrawals = 0d;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "active_status", nullable = false)
    private ActiveStatus activeStatus;


}
