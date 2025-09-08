package com.ikea.warehouse_command_api.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
@Schema(name = "ArticleResponse", description = "Article Inventory response payload")
public record ArticleResponse(
        @Schema(description = "Unique article identifier", example = "650f2c5f1a2b3c4d5e6f7a8b")
        String articleId,
        String name,
        Long stock,
        Long version,
        Instant createdDate,
        Instant lastModifiedDate
) {
}