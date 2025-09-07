package com.ikea.warehouse_command_api.data.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ArticleCommandRequest", description = "Article Inventory request object")
public record ArticleCommandRequest(
        @Schema(description = "Name of the inventory item", example = "leg")
        String name,
        @Schema(description = "Available stock quantity", example = "12")
        Long stock,
        @Schema(description = "Optional idempotency marker for last applied message id")
        String lastMessageId,
        @Schema(description = "Optional file created timestamp")
        Instant fileCreatedAt
) {
}