package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import org.bson.types.ObjectId;

public interface ProductService {
    ProductDocument insert(ProductDocument product);
    ProductDocument update(ObjectId id, ProductDocument updated, Long expectedVersion);
    void delete(ObjectId id, Long expectedVersion);
}
