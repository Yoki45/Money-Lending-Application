package com.lms.system.product.repository;

import com.lms.system.product.model.ProductFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProductFeeRepository  extends JpaRepository<ProductFee, Long> {
}
