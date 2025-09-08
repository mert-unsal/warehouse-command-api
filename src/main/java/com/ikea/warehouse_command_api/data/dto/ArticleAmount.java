package com.ikea.warehouse_command_api.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Article requirement specification for a product")
public record ArticleAmount(
    @NotEmpty
    @JsonProperty("art_id")
    @Schema(description = "Article identifier", example = "1")
    String artId,

    @Min(1)
    @JsonProperty("amount_of")
    @Schema(description = "Required quantity of this article", example = "4")
    Long amountOf
) {}
