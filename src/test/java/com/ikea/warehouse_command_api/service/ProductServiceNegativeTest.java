package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import com.ikea.warehouse_command_api.factory.ProductFactory;
import com.ikea.warehouse_command_api.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ProductServiceNegativeTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductFactory productFactory;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        String id = new ObjectId().toString();
        ProductCommandRequest request = new ProductCommandRequest("Chair", List.of());
        when(productRepository.findById(new ObjectId(id))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> productService.update(id, request));
    }
}
