package com.ikea.warehouse_command_api.repository;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface ArticleRepository extends MongoRepository<ArticleDocument, String> {

    @Query("{ '_id': ?0, 'stock': { $gte: ?1 } }")
    @Update("{ '$inc': { 'stock': ?#{-#amount}, 'version': 1 } }")
    Long decreaseStock(String articleId, Long amount);
}
