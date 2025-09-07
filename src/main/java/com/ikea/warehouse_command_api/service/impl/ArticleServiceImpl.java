package com.ikea.warehouse_command_api.service.impl;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.repository.ArticleRepository;
import com.ikea.warehouse_command_api.service.ArticleService;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;
    private final MongoTemplate mongoTemplate;

    public ArticleServiceImpl(ArticleRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Transactional
    public ArticleDocument insert(ArticleDocument article) {
        // ensure id is null so Mongo assigns a new one
        ArticleDocument toSave = new ArticleDocument(null, article.name(), article.stock(), article.lastMessageId(), null, article.createdDate(), article.lastModifiedDate(), article.fileCreatedAt());
        return repository.save(toSave);
    }

    @Override
    @Transactional
    public ArticleDocument update(ObjectId id, ArticleDocument updated, Long expectedVersion) {
        ArticleDocument existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));

        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
            throw new OptimisticLockingFailureException("Version mismatch for Article " + id + ": expected=" + expectedVersion + ", actual=" + existing.version());
        }

        ArticleDocument toSave = new ArticleDocument(
            existing.id(),
            updated.name() != null ? updated.name() : existing.name(),
            updated.stock() != null ? updated.stock() : existing.stock(),
            updated.lastMessageId() != null ? updated.lastMessageId() : existing.lastMessageId(),
            existing.version(),
            existing.createdDate(),
            existing.lastModifiedDate(),
            updated.fileCreatedAt() != null ? updated.fileCreatedAt() : existing.fileCreatedAt()
        );

        return repository.save(toSave);
    }

    @Override
    @Transactional
    public void delete(ObjectId id, Long expectedVersion) {
        ArticleDocument existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
            throw new OptimisticLockingFailureException("Version mismatch for Article " + id);
        }
        repository.deleteById(id);
    }
}
