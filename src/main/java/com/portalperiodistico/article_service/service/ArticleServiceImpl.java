package com.portalperiodistico.article_service.service;

import com.portalperiodistico.article_service.domain.dto.ArticleCreateRequest;
import com.portalperiodistico.article_service.domain.dto.ArticleDto;
import com.portalperiodistico.article_service.domain.dto.ArticleStatusDto;
import com.portalperiodistico.article_service.domain.dto.AuthorDto;
import com.portalperiodistico.article_service.domain.entity.Article;
import com.portalperiodistico.article_service.domain.entity.ArticleStatus;
import com.portalperiodistico.article_service.domain.repository.ArticleRepository;
import com.portalperiodistico.article_service.domain.repository.ArticleStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleStatusRepository articleStatusRepository;

    private static final String STATUS_DRAFT = "Borrador";
    private static final String STATUS_PUBLISHED = "Publicado";

    @Override
    @Transactional
    public ArticleDto createDraftArticle(ArticleCreateRequest createRequest, Integer authenticatedUserId) {

        // 1. Buscar el estado "Borrador"
        ArticleStatus draftStatus = articleStatusRepository.findByStatusName(STATUS_DRAFT)
                .orElseThrow(() -> new RuntimeException("Estado '" + STATUS_DRAFT + "' no configurado en la BD"));

        // 2. Crear el nuevo articulo
        Article newArticle = new Article();
        newArticle.setTitle(createRequest.getTitle());
        newArticle.setContent(createRequest.getContent());
        newArticle.setAuthorId(authenticatedUserId);
        newArticle.setArticleStatus(draftStatus);
        newArticle.setCurrentApprovalPercentage(BigDecimal.ZERO);

        // 3. Guardar el articulo
        Article savedArticle = articleRepository.save(newArticle);

        // 4. Convertir a DTO y retornar
        return mapToArticleDto(savedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getAllPublishedArticles() {
        List<Article> publishedArticles = articleRepository.findAllByArticleStatus_StatusName(STATUS_PUBLISHED);
        return publishedArticles.stream()
                .map(this::mapToArticleDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getArticlesByAuthor(Integer authorId) {
        List<Article> authorArticles = articleRepository.findAllByAuthorId(authorId);

        return authorArticles.stream()
                .map(this::mapToArticleDto)
                .collect(Collectors.toList());
    }

    // --- MAPPERS PRIVADOS ---

    private ArticleDto mapToArticleDto(Article article) {
        return new ArticleDto(
                article.getIdArticle(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                mapToAuthorDto(article.getAuthorId()),
                mapToStatusDto(article.getArticleStatus())
        );
    }

    private AuthorDto mapToAuthorDto(Integer authorId) {
        return new AuthorDto(
                authorId,
                null,  // username
                null,  // firstName
                null   // lastName
        );
    }

    private ArticleStatusDto mapToStatusDto(ArticleStatus status) {
        return new ArticleStatusDto(
                status.getIdArticleStatus(),
                status.getStatusName()
        );
    }
}