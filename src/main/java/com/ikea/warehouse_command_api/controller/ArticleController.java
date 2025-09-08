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
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}")
    @Operation(summary = "Update an existing article with optimistic locking")
    public ResponseEntity<ArticleResponse> update(@PathVariable("id") String id,
                                                  @RequestBody @Valid ArticleCommandRequest articleCommandRequest) {
        ArticleResponse updatedArticleDocument = articleService.update(id, articleCommandRequest);
        return ResponseEntity.ok(updatedArticleDocument);
    }

    @PostMapping("/{id}/decrease")
    @Operation(summary = "Decrease an amount of existing article with optimistic locking")
    public ResponseEntity<Void> decreaseStock(@PathVariable("id") String id,
                                                         @RequestBody @Valid ArticleCommandRequest articleCommandRequest) {
        articleService.decreaseAmount(id, articleCommandRequest);
        return ResponseEntity.ok().build();
    }

}
