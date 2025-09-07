package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import org.bson.types.ObjectId;

public interface ArticleService {
    ArticleDocument insert(ArticleDocument article);
    ArticleDocument update(ObjectId id, ArticleDocument updated, Long expectedVersion);
    void delete(ObjectId id, Long expectedVersion);
}
