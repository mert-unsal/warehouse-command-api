package com.ikea.warehouse_command_api.service;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import com.ikea.warehouse_command_api.data.repository.ArticleRepository;
import com.ikea.warehouse_command_api.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @Transactional
    public ArticleResponse save(ArticleCommandRequest articleCommandRequest) {
        ArticleDocument articleDocument = articleMapper.toDocument(articleCommandRequest);
        ArticleDocument persistedArticleDocument = articleRepository.save(articleDocument);
        return articleMapper.toResponse(persistedArticleDocument);
    }

//    @Transactional
//    public ArticleResponse update(String id, ArticleCommandRequest articleCommandRequest, Long expectedVersion) {
//        ArticleDocument existingArticleDocument = articleRepository.findById(new ObjectId(id))
//            .orElseThrow(() -> new IllegalArgumentException(STR."Article not found: \{id}"));
//
//        if (expectedVersion != null && existingArticleDocument.version() != null && !existingArticleDocument.version().equals(expectedVersion)) {
//            throw new OptimisticLockingFailureException(STR."Version mismatch for Article \{id}: expected=\{expectedVersion}, actual=\{existingArticleDocument.version()}");
//        }
//
//        ArticleDocument merged = new ArticleDocument(
//                existingArticleDocument.id(),
//                Optional.ofNullable(articleCommandRequest).map(ArticleCommandRequest::name).orElse(existingArticleDocument.name()),
//                Optional.ofNullable(articleCommandRequest).map(ArticleCommandRequest::stock).orElse(existingArticleDocument.stock()),
//                Optional.ofNullable(articleCommandRequest).map(ArticleCommandRequest::lastMessageId).orElse(existingArticleDocument.lastMessageId()),
//                existingArticleDocument.version(),
//                existingArticleDocument.createdDate(),
//                existingArticleDocument.lastModifiedDate(),
//                Optional.ofNullable(articleCommandRequest).map(ArticleCommandRequest::fileCreatedAt).orElse(existingArticleDocument.fileCreatedAt())
//        );
//
//        ArticleDocument persistedArticleDocument = articleRepository.save(merged);
//        return articleMapper.toResponse(persistedArticleDocument);
//
//    }
//
//    @Transactional
//    public void delete(ObjectId id, Long expectedVersion) {
//        ArticleDocument existing = articleRepository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
//        if (expectedVersion != null && existing.version() != null && !existing.version().equals(expectedVersion)) {
//            throw new OptimisticLockingFailureException("Version mismatch for Article " + id);
//        }
//        articleRepository.deleteById(id);
//    }
}
