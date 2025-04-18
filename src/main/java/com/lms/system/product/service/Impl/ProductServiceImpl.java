package com.lms.system.product.service.Impl;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.product.dto.ProductDTO;
import com.lms.system.product.dto.ProductFeeDTO;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductFeeRepository;
import com.lms.system.product.repository.ProductRepository;
import com.lms.system.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {


    private final ProductRepository productRepository;

    private final ProductFeeRepository productFeeRepository;

    private final ILocalizationService localizationService;

    @Override
    public String createProduct(ProductDTO productDTO) {

        if (productDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }


        Product product = Product.builder()
                .name(productDTO.getName())
                .tenureType(productDTO.getTenureType())
                .tenureValue(productDTO.getTenureValue())
                .build();

        product = productRepository.save(product);

        Product finalProduct = product;
        List<ProductFee> feeEntities = productDTO.getFees().stream()
                .map(dto -> ProductFee.builder()
                        .product(finalProduct)
                        .feeType(dto.getFeeType())
                        .amount(dto.getAmount())
                        .isPercentage(dto.getIsPercentage())
                        .applyOnDisbursement(dto.getApplyOnDisbursement())
                        .triggerDaysAfterDue(dto.getTriggerDaysAfterDue())
                        .build())
                .toList();

        productFeeRepository.saveAll(feeEntities);
        product.setFees(feeEntities);

        return localizationService.getMessage("message.product.createdSuccess", null);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(localizationService.getMessage("message.product.NotFound", null)));
        return mapProductToDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapProductToDTO).toList();
    }

    @Override
    public String deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(localizationService.getMessage("message.product.NotFound", null)));

        product.setActiveStatus(ActiveStatus.INACTIVE);

        productRepository.delete(product);
        return localizationService.getMessage("message.product.deletedSuccess", null);
    }

    @Override
    public String updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(localizationService.getMessage("message.product.NotFound", null)));

        product.setName(productDTO.getName());
        product.setTenureType(productDTO.getTenureType());
        product.setTenureValue(productDTO.getTenureValue());

        List<ProductFee> existingFees = product.getFees();
        for (ProductFee fee : existingFees) {
            fee.setActiveStatus(ActiveStatus.INACTIVE);
            productFeeRepository.save(fee);
        }

        List<ProductFee> updatedFees = productDTO.getFees().stream()
                .map(dto -> ProductFee.builder()
                        .product(product)
                        .feeType(dto.getFeeType())
                        .amount(dto.getAmount())
                        .isPercentage(dto.getIsPercentage())
                        .applyOnDisbursement(dto.getApplyOnDisbursement())
                        .triggerDaysAfterDue(dto.getTriggerDaysAfterDue())
                        .build())
                .toList();
        productFeeRepository.saveAll(updatedFees);

        product.setFees(updatedFees);
        productRepository.save(product);

        return localizationService.getMessage("message.product.updateSuccess", null);
    }

    private ProductDTO mapProductToDTO(Product product) {
        List<ProductFeeDTO> feeDTOs = product.getFees().stream()
                .map(fee -> ProductFeeDTO.builder()
                        .feeType(fee.getFeeType())
                        .amount(fee.getAmount())
                        .isPercentage(fee.getIsPercentage())
                        .applyOnDisbursement(fee.getApplyOnDisbursement())
                        .triggerDaysAfterDue(fee.getTriggerDaysAfterDue())
                        .build())
                .toList();

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .tenureType(product.getTenureType())
                .tenureValue(product.getTenureValue())
                .fees(feeDTOs)
                .build();
    }


}
