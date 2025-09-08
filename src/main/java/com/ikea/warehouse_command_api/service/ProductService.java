package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.repository.ProductRepository;
import com.ikea.warehouse_command_api.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.ikea.warehouse_command_api.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse save(@Valid ProductCommandRequest productCommandRequest) {
        ProductDocument productDocument = productMapper.toProductInsertDocument(productCommandRequest);
        ProductDocument persistedProductDocument = productRepository.save(productDocument);
        return productMapper.toResponse(persistedProductDocument);
    }

    @Transactional
    public ProductResponse update(String id, ProductCommandRequest productCommandRequest) {
        ProductDocument existingProductDocument = productRepository.findById(new ObjectId(id)).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND.concat(id)));

        Long existingVersionId = Optional.ofNullable(existingProductDocument).map(ProductDocument::version).orElse(null);
        if (Objects.equals(productCommandRequest,existingVersionId)) {
            throw new OptimisticLockingFailureException(VERSION_MISMATCH.concat(productCommandRequest.toString())
                    .concat(REQUIRED_VERSION)
                    .concat(existingVersionId.toString()));
        }

        ProductDocument productDocument = ProductDocument
                .builder()
                .id(existingProductDocument.id())
                .name(Optional.of(productCommandRequest).map(ProductCommandRequest::name).orElse(existingProductDocument.name()))
                .containArticles(Optional.of(productCommandRequest).map(ProductCommandRequest::containArticles).orElse(existingProductDocument.containArticles()))
                .build();

        ProductDocument persistedProductDocument = productRepository.save(productDocument);
        return productMapper.toResponse(persistedProductDocument);
    }
//
//    @Transactional
//    public void delete(ObjectId id, Long expectedVersion) {
//        ProductDocument existing = repository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
//        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
//            throw new OptimisticLockingFailureException("Version mismatch for Product " + id);
//        }
//        repository.deleteById(id);
//    }
//
//    @Transactional
//    public void sell(ObjectId id, int quantity, Long expectedVersion, String idempotencyKey) {
//        // Minimal implementation to satisfy the endpoint contract.
//        // Validate product exists and versions if provided. Inventory adjustments can be implemented later.
//        ProductDocument existing = repository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException(STR."Product not found: \{id}"));
//        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
//            throw new OptimisticLockingFailureException("Version mismatch for Product " + id + ": expected=" + expectedVersion + ", actual=" + existing.version());
//        }
//        // Idempotency handling and inventory deduction are out of scope for this minimal change.
//    }

}
