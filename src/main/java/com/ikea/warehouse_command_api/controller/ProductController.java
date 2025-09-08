package com.ikea.warehouse_command_api.controller;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete a product with optimistic locking")
//    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
//        productService.delete(new ObjectId(id), expectedVersion);
//        return ResponseEntity.noContent().build();
//    }
////
//    @PostMapping("/{id}/sell")
//    @Operation(summary = "Sell a product and update inventory with optimistic locking and idempotency")
//    public ResponseEntity<Void> sell(@PathVariable("id") String id,
//                                     @RequestParam("quantity") @Min(1) int quantity)
//        productService.sell(new ObjectId(id), quantity, expectedVersion, idempotencyKey);
//        return ResponseEntity.noContent().build();
//    }
}
