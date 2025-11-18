package com.portalperiodistico.article_service.service;

import com.portalperiodistico.article_service.domain.dto.ArticleCreateRequest;
import com.portalperiodistico.article_service.domain.dto.ArticleDto;

import java.util.List;

public interface ArticleService {

    /**
     * Crea un nuevo artículo. El artículo se guarda inicialmente como "Borrador".
     *
     * @param createRequest El DTO con los datos del artículo (título, contenido).
     * @param authenticatedUserId El ID del usuario autenticado (autor).
     * @return El DTO del artículo creado.
     */
    ArticleDto createDraftArticle(ArticleCreateRequest createRequest, Integer authenticatedUserId);

    /**
     * Obtiene todos los artículos públicos (estado "Publicado").
     *
     * @return Lista de DTOs de artículos publicados.
     */
    List<ArticleDto> getAllPublishedArticles();

    /**
     * Obtiene los artículos escritos por un autor específico.
     *
     * @param authorId El ID del autor.
     * @return Lista de DTOs de artículos de ese autor.
     */
    List<ArticleDto> getArticlesByAuthor(Integer authorId);

    // (Aún no implementaremos la actualización (PUT), pero así se vería)
    /*
    ArticleDto updateArticle(Integer articleId, ArticleUpdateRequest updateRequest, Long authenticatedUserId);
    */
}