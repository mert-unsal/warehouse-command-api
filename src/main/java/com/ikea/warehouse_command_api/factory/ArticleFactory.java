package com.ikea.warehouse_command_api.factory;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import org.springframework.stereotype.Service;

@Service
public class ArticleFactory {

    public ArticleResponse toArticleResponse(ArticleDocument articleDocument) {
        return ArticleResponse.builder()
                .articleId(articleDocument.id())
                .name(articleDocument.name())
                .stock(articleDocument.stock())
                .createdDate(articleDocument.createdDate())
                .lastModifiedDate(articleDocument.lastModifiedDate())
                .version(articleDocument.version())
                .build();
    }

    public ArticleDocument toArticleDocument(ArticleDocument existingArticleDocument, ArticleCommandRequest articleCommandRequest) {
        return existingArticleDocument.toBuilder()
                .name(articleCommandRequest.name())
                .stock(articleCommandRequest.stock())
                .build();
    }

    public ArticleDocument toArticleDocument(ArticleCommandRequest articleCommandRequest) {
        return ArticleDocument.builder()
                .name(articleCommandRequest.name())
                .stock(articleCommandRequest.stock())
                .build();
    }
}
