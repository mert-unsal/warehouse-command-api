package com.ikea.warehouse_command_api.data.request;

import com.ikea.warehouse_command_api.data.dto.ArticleAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(name = "ProductCommandRequest", description = "Request payload to create or update a product")
public record ProductCommandRequest(
        @NotBlank
        @Schema(description = "Product name", example = "Dining Chair")
        String name,
        @NotEmpty
        @Valid
        @Schema(description = "List of articles required to build this product")
        List<ArticleAmount> containArticles
) {}
