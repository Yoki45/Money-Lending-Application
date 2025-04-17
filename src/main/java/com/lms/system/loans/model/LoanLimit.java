package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import jakarta.persistence.*;
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
}
