package com.ikea.warehouse_command_api.controller;

import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import com.ikea.warehouse_command_api.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Article Command Operations")
@RequestMapping("/api/v1/commands/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @Operation(summary = "Insert a new article")
    public ResponseEntity<ArticleResponse> insert(@RequestBody @Valid ArticleCommandRequest articleCommandRequest) {
        ArticleResponse articleResponse = articleService.save(articleCommandRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponse);
    }

//    @PutMapping("/{id}")
//    @Operation(summary = "Update an existing article with optimistic locking")
//    public ResponseEntity<ArticleResponse> update(@PathVariable("id") String id,
//                                                  @RequestParam(value = "expectedVersion", required = false) Long expectedVersion,
//                                                  @RequestBody @jakarta.validation.Valid ArticleCommandRequest request) {
//        ArticleResponse updatedArticleDocument = articleService.update(id, request, expectedVersion);
//        return ResponseEntity.ok(updatedArticleDocument);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete an article with optimistic locking")
//    public ResponseEntity<Void> delete(@PathVariable("id") String id,
//                                       @RequestParam(value = "expectedVersion", required = false) Long expectedVersion) {
//        articleService.delete(new ObjectId(id), expectedVersion);
//        return ResponseEntity.noContent().build();
//    }
}
