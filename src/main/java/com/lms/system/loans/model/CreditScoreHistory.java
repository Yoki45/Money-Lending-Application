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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CreditScore creditScore;

    @Column(name = "score_value")
    private Double  score = 0d;

}
