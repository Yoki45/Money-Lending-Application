package com.lms.system.product.model;


import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.product.enums.TenureType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name="products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenure_type")
    private TenureType tenureType;

    @Column(name = "tenure_value")
    private Integer tenureValue;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductFee> fees = new ArrayList<>();

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "active_status", nullable = false)
    private ActiveStatus activeStatus;

}
