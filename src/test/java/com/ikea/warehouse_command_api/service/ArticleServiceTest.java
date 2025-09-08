package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import com.ikea.warehouse_command_api.factory.ArticleFactory;
import com.ikea.warehouse_command_api.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleFactory articleFactory;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldPersistAndReturnResponse() {
        ArticleCommandRequest request = new ArticleCommandRequest("1","leg", 5L);
        ArticleDocument toSave = ArticleDocument.builder().name("leg").stock(5L).build();
        ArticleDocument saved = ArticleDocument.builder().id("507f1f77bcf86cd799439011").name("leg").stock(5L).build();
        ArticleResponse expected = ArticleResponse.builder().articleId("507f1f77bcf86cd799439011").name("leg").stock(5L).build();

        when(articleFactory.toArticleDocument(request)).thenReturn(toSave);
        when(articleRepository.save(toSave)).thenReturn(saved);
        when(articleFactory.toArticleResponse(saved)).thenReturn(expected);

        ArticleResponse actual = articleService.save(request);

        assertEquals(expected, actual);
        verify(articleRepository).save(toSave);
    }

    @Test
    void update_shouldFindUpdateAndReturnResponse() {
        String id = "507f1f77bcf86cd799439011";
        ArticleCommandRequest request = new ArticleCommandRequest("1","leg", 10L);
        ArticleDocument existing = ArticleDocument.builder().id(id).name("leg").stock(5L).build();
        ArticleDocument updated = ArticleDocument.builder().id(id).name("leg").stock(10L).build();
        ArticleResponse expected = ArticleResponse.builder().articleId(id).name("leg").stock(10L).build();

        when(articleRepository.findById(any())).thenReturn(Optional.of(existing));
        when(articleFactory.toArticleDocument(existing, request)).thenReturn(updated);
        when(articleRepository.save(updated)).thenReturn(updated);
        when(articleFactory.toArticleResponse(updated)).thenReturn(expected);

        ArticleResponse actual = articleService.update(id, request);
        assertEquals(expected, actual);
    }

    @Test
    void decreaseAmount_shouldThrowWhenNoStockProvided() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> articleService.decreaseAmount("id", new ArticleCommandRequest("1","leg", null)));
        assertTrue(ex.getMessage().contains("Decrease amount must be provided"));
    }

    @Test
    void decreaseAmount_shouldCallRepositoryAndSucceed() {
        when(articleRepository.decreaseStock(eq("abc"), eq(3L))).thenReturn(1L);
        assertDoesNotThrow(() -> articleService.decreaseAmount("abc", new ArticleCommandRequest("1","leg", 3L)));
        verify(articleRepository).decreaseStock("abc", 3L);
    }

    @Test
    void decreaseAmount_shouldThrowWhenUpdatedZero() {
        when(articleRepository.decreaseStock(eq("abc"), eq(3L))).thenReturn(0L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> articleService.decreaseAmount("abc", new ArticleCommandRequest("1","leg", 3L)));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }
}
