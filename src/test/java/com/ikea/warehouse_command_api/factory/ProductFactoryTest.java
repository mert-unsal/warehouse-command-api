package com.ikea.warehouse_command_api.factory;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ArticleAmount;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductFactoryTest {

    private final ProductFactory factory = new ProductFactory();

    @Test
    void toProductInsertDocument_mapsFields() {
        ProductCommandRequest req = new ProductCommandRequest("Table", List.of(new ArticleAmount("a1", 2L)));
        ProductDocument doc = factory.toProductInsertDocument(req);
        assertNull(doc.id());
        assertEquals("Table", doc.name());
        assertEquals(1, doc.containArticles().size());
        assertEquals("a1", doc.containArticles().getFirst().artId());
        assertEquals(2L, doc.containArticles().getFirst().amountOf());
    }

    @Test
    void toProductDocument_updatesExisting() {
        ProductDocument existing = ProductDocument.builder()
                .id(new ObjectId())
                .name("Old")
                .containArticles(List.of(new ArticleAmount("x", 1L)))
                .version(1L)
                .createdDate(Instant.now())
                .lastModifiedDate(Instant.now())
                .build();
        ProductCommandRequest req = new ProductCommandRequest("New", List.of(new ArticleAmount("a1", 3L)));
        ProductDocument updated = factory.toProductDocument(existing, req);
        assertEquals(existing.id(), updated.id());
        assertEquals("New", updated.name());
        assertEquals(3L, updated.containArticles().getFirst().amountOf());
    }

    @Test
    void toProductResponse_mapsAllFields() {
        Instant created = Instant.now();
        Instant modified = created.plusSeconds(60);
        ProductDocument saved = ProductDocument.builder()
                .id(new ObjectId())
                .name("Chair")
                .containArticles(List.of(new ArticleAmount("a1", 2L)))
                .version(2L)
                .createdDate(created)
                .lastModifiedDate(modified)
                .build();
        ProductResponse resp = factory.toProductResponse(saved);
        assertEquals(saved.id().toString(), resp.id());
        assertEquals("Chair", resp.name());
        assertEquals(2L, resp.version());
        assertEquals(created, resp.createdDate());
        assertEquals(modified, resp.lastModifiedDate());
        assertNull(resp.createdBy());
        assertNull(resp.lastModifiedBy());
    }
}
