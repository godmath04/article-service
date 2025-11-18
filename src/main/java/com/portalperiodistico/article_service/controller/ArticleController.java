package com.portalperiodistico.article_service.controller;

import com.portalperiodistico.article_service.domain.dto.ArticleCreateRequest;
import com.portalperiodistico.article_service.domain.dto.ArticleDto;
import com.portalperiodistico.article_service.security.UserPrincipal;
import com.portalperiodistico.article_service.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Crear un nuevo articulo (Borrador)
     * POST /api/v1/articles
     * Requiere autenticacion
     */
    @PostMapping
    public ResponseEntity<ArticleDto> createDraftArticle(
            @Valid @RequestBody ArticleCreateRequest createRequest,
            @AuthenticationPrincipal UserPrincipal authenticatedUser) {

        System.out.println("=== DEBUG POST ARTICLE ===");
        System.out.println("authenticatedUser: " + authenticatedUser);
        System.out.println("userId: " + (authenticatedUser != null ? authenticatedUser.getUserId() : "null"));

        if (authenticatedUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ArticleDto createdArticle = articleService.createDraftArticle(
                createRequest,
                authenticatedUser.getUserId()
        );

        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }

    /**
     * Obtener todos los articulos publicados
     * GET /api/v1/articles
     * Publico (no requiere autenticacion)
     */
    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllPublishedArticles() {
        List<ArticleDto> articles = articleService.getAllPublishedArticles();
        return ResponseEntity.ok(articles);
    }

    /**
     * Obtener articulos por autor
     * GET /api/v1/articles/author/{authorId}
     * Publico (no requiere autenticacion)
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleDto>> getArticlesByAuthor(@PathVariable Integer authorId) {
        List<ArticleDto> articles = articleService.getArticlesByAuthor(authorId);
        return ResponseEntity.ok(articles);
    }
}