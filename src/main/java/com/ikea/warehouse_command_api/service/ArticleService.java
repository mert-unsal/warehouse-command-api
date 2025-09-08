package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import com.ikea.warehouse_command_api.factory.ArticleFactory;
import com.ikea.warehouse_command_api.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleFactory articleFactory;

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2.0)
    )
    @Transactional
    public ArticleResponse save(ArticleCommandRequest articleCommandRequest) {
        ArticleDocument articleDocument = articleFactory.toArticleDocument(articleCommandRequest);
        ArticleDocument persistedArticleDocument = articleRepository.save(articleDocument);
        return articleFactory.toArticleResponse(persistedArticleDocument);
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2.0)
    )
    @Transactional
    public ArticleResponse update(String id, ArticleCommandRequest articleCommandRequest) {
        ArticleDocument existingArticleDocument = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(STR."Article not found: \{id}"));
        ArticleDocument updatedArticleDocument = articleFactory.toArticleDocument(existingArticleDocument, articleCommandRequest);
        ArticleDocument persistedArticleDocument = articleRepository.save(updatedArticleDocument);
        return articleFactory.toArticleResponse(persistedArticleDocument);
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2.0)
    )
    @Transactional
    public void decreaseAmount(String id, ArticleCommandRequest articleCommandRequest) {
        if (articleCommandRequest == null || articleCommandRequest.stock() == null) {
            throw new IllegalArgumentException("Decrease amount must be provided");
        }
        Long updated = articleRepository.decreaseStock(id, articleCommandRequest.stock());
        if (updated == null || updated == 0) {
            throw new IllegalArgumentException("Insufficient stock to decrease or article not found");
        }
    }
}
