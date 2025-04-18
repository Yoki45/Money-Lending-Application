package com.lms.system.product.model;


import com.lms.generic.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
}
