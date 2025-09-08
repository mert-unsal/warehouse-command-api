
package com.ikea.warehouse_command_api.factory;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class ProductFactory {

    public ProductDocument toProductInsertDocument(@Valid ProductCommandRequest productCommandRequest) {
        return ProductDocument.builder()
                .name(productCommandRequest.name())
                .containArticles(productCommandRequest.containArticles())
                .build();
    }

    public ProductDocument toProductDocument(ProductDocument existingProductDocument, ProductCommandRequest productCommandRequest) {
        return existingProductDocument.toBuilder()
                .name(productCommandRequest.name())
                .containArticles(productCommandRequest.containArticles())
                .build();
    }

    public ProductResponse toProductResponse(ProductDocument persistedProductDocument) {
        return new ProductResponse(
                persistedProductDocument.id() != null ? persistedProductDocument.id().toString() : null,
                persistedProductDocument.name(),
                persistedProductDocument.containArticles(),
                persistedProductDocument.version(),
                persistedProductDocument.createdDate(),
                persistedProductDocument.lastModifiedDate(),
                null,
                null
        );
    }
}
