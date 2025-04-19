package com.lms.system.loan.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.user.model.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Table(name="credit_scores")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScore extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score")
    private Double  score  = 0d;

    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private User customer;
}
