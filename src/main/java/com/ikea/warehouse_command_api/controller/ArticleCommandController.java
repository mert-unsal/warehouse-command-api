package com.ikea.warehouse_command_api.controller;

import com.ikea.warehouse_command_api.mapper.ArticleMapper;
import com.ikea.warehouse_command_api.data.dto.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.dto.ArticleResponse;
import com.ikea.warehouse_command_api.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands/articles")
@Tag(name = "Article Commands")
public class ArticleCommandController {

    private final ArticleService service;
    private final ArticleMapper mapper;

    public ArticleCommandController(ArticleService service, ArticleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Insert a new article")
    public ResponseEntity<ArticleResponse> insert(@RequestBody @jakarta.validation.Valid ArticleCommandRequest request) {
        ArticleDocument toInsert = mapper.toDocument(request);
        ArticleDocument created = service.insert(toInsert);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing article with optimistic locking")
    public ResponseEntity<ArticleResponse> update(@PathVariable("id") String id,
                                                  @RequestParam(value = "expectedVersion", required = false) Long expectedVersion,
                                                  @RequestBody @jakarta.validation.Valid ArticleCommandRequest request) {
        ArticleDocument updatedDoc = service.update(new ObjectId(id), mapper.toDocument(request), expectedVersion);
        return ResponseEntity.ok(mapper.toResponse(updatedDoc));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an article with optimistic locking")
    public ResponseEntity<Void> delete(@PathVariable("id") String id,
                                       @RequestParam(value = "expectedVersion", required = false) Long expectedVersion) {
        service.delete(new ObjectId(id), expectedVersion);
        return ResponseEntity.noContent().build();
    }
}
