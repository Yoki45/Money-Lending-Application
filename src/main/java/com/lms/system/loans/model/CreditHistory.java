package com.lms.system.loans.model;

import com.lms.generic.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="credit_histories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditHistory  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;





}
