package com.portalperiodistico.article_service.controller;

// DTOs
import com.portalperiodistico.article_service.domain.dto.ArticleCreateRequest;
import com.portalperiodistico.article_service.domain.dto.ArticleDto;

// Entidad User (para inyección de seguridad)
import com.portalperiodistico.auth_service.domain.entity.User;

// Servicio
import com.portalperiodistico.article_service.service.ArticleService;

// Imports de Spring
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles") // Ruta base para todos los endpoints
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // --- ENDPOINT 1: Crear un Artículo (Protegido) ---
    // POST /api/v1/articles
    // Este endpoint SÍ requiere autenticación (como definimos en SecurityConfig)

    @PostMapping
    public ResponseEntity<ArticleDto> createDraftArticle(
            @Valid @RequestBody ArticleCreateRequest createRequest,
            @AuthenticationPrincipal User authenticatedUser) {

        System.out.println("=== DEBUG POST ARTICLE ===");
        System.out.println("authenticatedUser: " + authenticatedUser);
        System.out.println("authenticatedUser es null? " + (authenticatedUser == null));


        // @AuthenticationPrincipal: Spring Security inyecta aquí al usuario
        // que viene del token JWT. Es el 'User' que cargó UserDetailsServiceImpl.

        if (authenticatedUser == null) {
            // Guardia
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Llamamos al servicio pasando el ID del usuario extraído del token
        ArticleDto createdArticle = articleService.createDraftArticle(
                createRequest,
                authenticatedUser.getUserId()
        );

        // Devolvemos 201 Created y el artículo en el body
        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }

    // --- ENDPOINT 2: Obtener Artículos Publicados (Público) ---
    // GET /api/v1/articles
    // Este endpoint es PÚBLICO (como definimos en SecurityConfig)

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllPublishedArticles() {
        List<ArticleDto> articles = articleService.getAllPublishedArticles();
        return ResponseEntity.ok(articles);
    }

    // --- ENDPOINT 3: Obtener Artículos por Autor (Público) ---
    // GET /api/v1/articles/author/{authorId}
    // Este endpoint también es PÚBLICO

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleDto>> getArticlesByAuthor(@PathVariable Integer authorId) {
        List<ArticleDto> articles = articleService.getArticlesByAuthor(authorId);
        return ResponseEntity.ok(articles);
    }
}