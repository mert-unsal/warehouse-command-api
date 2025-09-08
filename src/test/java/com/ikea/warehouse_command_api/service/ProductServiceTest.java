package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ArticleAmount;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProductServiceTest {

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
    void save_shouldPersistAndReturnResponse() {
        ProductCommandRequest request = new ProductCommandRequest("Chair", List.of(new ArticleAmount("art1", 2L)));
        ProductDocument toSave = ProductDocument.builder().name("Chair").containArticles(request.containArticles()).build();
        ProductDocument saved = ProductDocument.builder().id(new ObjectId()).name("Chair").containArticles(request.containArticles()).build();
        ProductResponse expected = new ProductResponse(saved.id().toString(), "Chair", request.containArticles(), null, null, null, null, null);

        when(productFactory.toProductInsertDocument(request)).thenReturn(toSave);
        when(productRepository.save(toSave)).thenReturn(saved);
        when(productFactory.toProductResponse(saved)).thenReturn(expected);

        ProductResponse actual = productService.save(request);
        assertEquals(expected, actual);
    }

    @Test
    void update_shouldFindUpdateAndReturnResponse() {
        String id = new ObjectId().toString();
        ProductCommandRequest request = new ProductCommandRequest("Chair 2", List.of(new ArticleAmount("art1", 3L)));
        ProductDocument existing = ProductDocument.builder().id(new ObjectId(id)).name("Chair").containArticles(List.of(new ArticleAmount("art1", 2L))).build();
        ProductDocument updated = ProductDocument.builder().id(new ObjectId(id)).name("Chair 2").containArticles(request.containArticles()).build();
        ProductResponse expected = new ProductResponse(id, "Chair 2", request.containArticles(), null, null, null, null, null);

        when(productRepository.findById(new ObjectId(id))).thenReturn(Optional.of(existing));
        when(productFactory.toProductDocument(existing, request)).thenReturn(updated);
        when(productRepository.save(updated)).thenReturn(updated);
        when(productFactory.toProductResponse(updated)).thenReturn(expected);

        ProductResponse actual = productService.update(id, request);
        assertEquals(expected, actual);
    }
}
