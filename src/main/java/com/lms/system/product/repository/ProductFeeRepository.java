package com.lms.system.product.repository;

import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductFeeRepository  extends JpaRepository<ProductFee, Long> {

    List<ProductFee> findByProduct(Product product);
}
