package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import com.ikea.warehouse_command_api.factory.ProductFactory;
import com.ikea.warehouse_command_api.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ikea.warehouse_command_api.util.ErrorMessages.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductFactory productFactory;

    @Transactional
    public ProductResponse save(@Valid ProductCommandRequest productCommandRequest) {
        ProductDocument productDocument = productFactory.toProductInsertDocument(productCommandRequest);
        ProductDocument persistedProductDocument = productRepository.save(productDocument);
        return productFactory.toProductResponse(persistedProductDocument);
    }

    @Transactional
    public ProductResponse update(String id, ProductCommandRequest productCommandRequest) {
        ProductDocument existingProductDocument = productRepository.findById(new ObjectId(id)).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND.concat(id)));
        ProductDocument productDocument = productFactory.toProductDocument(existingProductDocument, productCommandRequest);
        ProductDocument persistedProductDocument = productRepository.save(productDocument);
        return productFactory.toProductResponse(persistedProductDocument);
    }

}
