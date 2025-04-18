package com.lms.system.product.service;

import com.lms.system.product.dto.ProductDTO;

import java.util.List;

public interface IProductService {

    String createProduct(ProductDTO productDTO);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    String deleteProduct(Long id);

    String updateProduct(Long id, ProductDTO productDTO);
}
