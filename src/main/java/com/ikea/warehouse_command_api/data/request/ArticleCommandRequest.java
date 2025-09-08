package com.ikea.warehouse_command_api.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(name = "ArticleCommandRequest", description = "Article Inventory request object")
public record ArticleCommandRequest(
        @JsonProperty("article_id")
        @Schema(description = "name of the article id", example = "1")
        String articleId,
        @Schema(description = "name of the article item", example = "leg")
        String name,
        @Schema(description = "Available stock quantity", example = "12")
        @Min(1)
        Long stock) {
}