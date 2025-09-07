package com.ikea.warehouse_command_api.data.repository;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<ProductDocument, ObjectId> {
}
