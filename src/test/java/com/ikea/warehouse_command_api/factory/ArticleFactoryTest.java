package com.ikea.warehouse_command_api.factory;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArticleFactoryTest {

    private final ArticleFactory factory = new ArticleFactory();

    @Test
    void toArticleResponse_mapsAllFields() {
        Instant created = Instant.now();
        Instant modified = created.plusSeconds(5);
        ArticleDocument doc = ArticleDocument.builder()
                .id("1")
                .name("leg")
                .stock(10L)
                .version(3L)
                .createdDate(created)
                .lastModifiedDate(modified)
                .build();
        ArticleResponse resp = factory.toArticleResponse(doc);
        assertEquals("1", resp.articleId());
        assertEquals("leg", resp.name());
        assertEquals(10L, resp.stock());
        assertEquals(3L, resp.version());
        assertEquals(created, resp.createdDate());
        assertEquals(modified, resp.lastModifiedDate());
    }

    @Test
    void toArticleDocument_updatesExisting() {
        ArticleDocument existing = ArticleDocument.builder()
                .id("1")
                .name("old")
                .stock(5L)
                .version(1L)
                .build();
        ArticleCommandRequest request = new ArticleCommandRequest("1", "new", 9L);
        ArticleDocument updated = factory.toArticleDocument(existing, request);
        assertEquals("1", updated.id());
        assertEquals("new", updated.name());
        assertEquals(9L, updated.stock());
        assertEquals(1L, updated.version());
    }

    @Test
    void toArticleDocument_fromRequest() {
        ArticleCommandRequest request = new ArticleCommandRequest("1", "leg", 12L);
        ArticleDocument newDoc = factory.toArticleDocument(request);
        assertNull(newDoc.id());
        assertEquals("leg", newDoc.name());
        assertEquals(12L, newDoc.stock());
    }
}
