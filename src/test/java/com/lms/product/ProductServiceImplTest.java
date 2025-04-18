package com.lms.product;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.product.dto.ProductDTO;
import com.lms.system.product.dto.ProductFeeDTO;
import com.lms.system.product.enums.FeeType;
import com.lms.system.product.enums.TenureType;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductFeeRepository;
import com.lms.system.product.repository.ProductRepository;
import com.lms.system.product.service.Impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductFeeRepository productFeeRepository;
    @Mock
    private ILocalizationService localizationService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private List<ProductFee> feeList;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(1L)
                .name("LoanX")
                .tenureType(TenureType.DAYS)
                .tenureValue(30)
                .fees(new ArrayList<>())
                .build();

        productDTO = ProductDTO.builder()
                .name("LoanX")
                .tenureType(TenureType.DAYS)
                .tenureValue(30)
                .fees(List.of(
                        ProductFeeDTO.builder()
                                .feeType(FeeType.LATE)
                                .amount(10.0)
                                .isPercentage(false)
                                .applyOnDisbursement(false)
                                .triggerDaysAfterDue(3)
                                .build()
                ))
                .build();

        feeList = List.of(
                ProductFee.builder()
                        .id(1L)
                        .product(product)
                        .feeType(FeeType.LATE)
                        .amount(10.0)
                        .isPercentage(false)
                        .applyOnDisbursement(false)
                        .triggerDaysAfterDue(3)
                        .build()
        );
    }

    @Test
    void createProduct_shouldCreateProductWithFees() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(localizationService.getMessage(anyString(), any())).thenReturn("Success");

        String result = productService.createProduct(productDTO);

        assertEquals("Success", result);
        verify(productRepository).save(any(Product.class));
        verify(productFeeRepository).saveAll(anyList());
    }

    @Test
    void createProduct_shouldThrowExceptionWhenDTOIsNull() {
        assertThrows(BadRequestException.class, () -> productService.createProduct(null));
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO dto = productService.getProductById(1L);

        assertNotNull(dto);
        assertEquals("LoanX", dto.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_shouldThrowExceptionIfNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        product.setActiveStatus(ActiveStatus.ACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(localizationService.getMessage(anyString(), any())).thenReturn("Success");

        String result = productService.deleteProduct(1L);

        assertEquals("Success", result);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowExceptionIfNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(2L));
    }

    @Test
    void updateProduct_shouldUpdateProductWithNewFees() {
        product.setFees(feeList);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(localizationService.getMessage(anyString(), any())).thenReturn("Success");

        String result = productService.updateProduct(1L, productDTO);

        assertEquals("Success", result);
        verify(productFeeRepository).saveAll(anyList());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_shouldThrowExceptionIfNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productDTO));
    }
}
