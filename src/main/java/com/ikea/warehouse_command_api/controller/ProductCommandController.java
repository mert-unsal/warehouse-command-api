package com.ikea.warehouse_command_api.controller;

import com.ikea.warehouse_command_api.mapper.ProductMapper;
import com.ikea.warehouse_command_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands/products")
@Tag(name = "Product Commands")
public class ProductCommandController {

    private final ProductService service;
    private final ProductMapper mapper;

    public ProductCommandController(ProductService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Insert a new product")
    public ResponseEntity<ProductResponse> insert(@RequestBody @jakarta.validation.Valid ProductCommandRequest request) {
        ProductDocument toInsert = mapper.toDocument(request);
        ProductDocument created = service.insert(toInsert);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product with optimistic locking")
    public ResponseEntity<ProductResponse> update(@PathVariable("id") String id,
                                                  @RequestParam(value = "expectedVersion", required = false) Long expectedVersion,
                                                  @RequestBody @jakarta.validation.Valid ProductCommandRequest request) {
        ProductDocument updated = service.update(new ObjectId(id), mapper.toDocument(request), expectedVersion);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product with optimistic locking")
    public ResponseEntity<Void> delete(@PathVariable("id") String id,
                                       @RequestParam(value = "expectedVersion", required = false) Long expectedVersion) {
        service.delete(new ObjectId(id), expectedVersion);
        return ResponseEntity.noContent().build();
    }
}
