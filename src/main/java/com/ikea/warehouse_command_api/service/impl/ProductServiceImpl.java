package com.ikea.warehouse_command_api.service.impl;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.repository.ProductRepository;
import com.ikea.warehouse_command_api.service.ProductService;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProductDocument insert(ProductDocument product) {
        ProductDocument toSave = new ProductDocument(null, product.name(), product.containArticles(), null, product.createdDate(), product.lastModifiedDate(), product.createdBy(), product.lastModifiedBy());
        return repository.save(toSave);
    }

    @Override
    @Transactional
    public ProductDocument update(ObjectId id, ProductDocument updated, Long expectedVersion) {
        ProductDocument existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
            throw new OptimisticLockingFailureException("Version mismatch for Product " + id + ": expected=" + expectedVersion + ", actual=" + existing.version());
        }

        ProductDocument toSave = new ProductDocument(
            existing.id(),
            updated.name() != null ? updated.name() : existing.name(),
            updated.containArticles() != null ? updated.containArticles() : existing.containArticles(),
            existing.version(),
            existing.createdDate(),
            existing.lastModifiedDate(),
            existing.createdBy(),
            existing.lastModifiedBy()
        );
        return repository.save(toSave);
    }

    @Override
    @Transactional
    public void delete(ObjectId id, Long expectedVersion) {
        ProductDocument existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
            throw new OptimisticLockingFailureException("Version mismatch for Product " + id);
        }
        repository.deleteById(id);
    }
}
