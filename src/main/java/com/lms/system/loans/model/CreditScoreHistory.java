package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="credit_score_histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScoreHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "credit_score", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private CreditScore creditScore;

    @Column(name = "limit")
    private Double  score = 0d;

}
