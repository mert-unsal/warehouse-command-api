package com.ikea.warehouse_command_api.controller;

import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import com.ikea.warehouse_command_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commands/products")
@Tag(name = "Product Command Operations")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Save a new product")
    public ResponseEntity<ProductResponse> save(@RequestBody @Valid ProductCommandRequest productCommandRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productCommandRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product with optimistic locking")
    public ResponseEntity<ProductResponse> update(@PathVariable("id") @NotEmpty String id,
                                                  @RequestBody @Valid ProductCommandRequest productUpdateRequest) {
        ProductResponse productResponse = productService.update(id,productUpdateRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/{id}/sell")
    @Operation(summary = "Sell a product and update its stock according to the product definition")
    public ResponseEntity<Void> decrease(@PathVariable("id") @NotEmpty String id,
                                         @RequestParam @Min(1) Long count) {
        productService.decreaseStock(id, count);
        return ResponseEntity.ok().build();
    }

}
