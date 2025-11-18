package com.portalperiodistico.article_service.service;

// (Todos los imports siguen igual)
import com.portalperiodistico.article_service.domain.dto.ArticleCreateRequest;
import com.portalperiodistico.article_service.domain.dto.ArticleDto;
import com.portalperiodistico.article_service.domain.dto.ArticleStatusDto;
import com.portalperiodistico.article_service.domain.dto.AuthorDto;
import com.portalperiodistico.article_service.domain.entity.Article;
import com.portalperiodistico.article_service.domain.entity.ArticleStatus;
import com.portalperiodistico.auth_service.domain.entity.User;
import com.portalperiodistico.article_service.domain.repository.ArticleRepository;
import com.portalperiodistico.article_service.domain.repository.ArticleStatusRepository;
import com.portalperiodistico.auth_service.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleStatusRepository articleStatusRepository;
    private final UserRepository userRepository;

    private static final String STATUS_DRAFT = "Borrador";
    private static final String STATUS_PUBLISHED = "Publicado";

    // La interfaz 'ArticleService' ya usa 'Integer', así que esto coincide
    @Override
    @Transactional
    public ArticleDto createDraftArticle(ArticleCreateRequest createRequest, Integer authenticatedUserId) {

        // 1. Buscar al autor (User)
        // 'userRepository.findById' acepta 'Integer' (ya que User.userId es Integer)
        User author = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("Usuario autor no encontrado con ID: " + authenticatedUserId));

        // ... (Pasos 2, 3 y 4 sin cambios) ...
        ArticleStatus draftStatus = articleStatusRepository.findByStatusName(STATUS_DRAFT)
                .orElseThrow(() -> new RuntimeException("Estado '" + STATUS_DRAFT + "' no configurado en la BD"));

        Article newArticle = new Article();
        newArticle.setTitle(createRequest.getTitle());
        newArticle.setContent(createRequest.getContent());
        newArticle.setAuthor(author);
        newArticle.setArticleStatus(draftStatus);

        Article savedArticle = articleRepository.save(newArticle);

        // 5. Convertir la entidad a un DTO de respuesta y retornarlo
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
        // 1. Buscar artículos por ID de autor
        // CAMBIO: Llamamos al método corregido del repositorio
        List<Article> authorArticles = articleRepository.findAllByAuthor_UserId(authorId);

        // 2. Convertir a DTOs
        return authorArticles.stream()
                .map(this::mapToArticleDto)
                .collect(Collectors.toList());
    }


    // --- MAPPERS PRIVADOS (CORREGIDOS) ---

    private ArticleDto mapToArticleDto(Article article) {
        return new ArticleDto(
                article.getIdArticle(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                mapToAuthorDto(article.getAuthor()),     // Helper corregido
                mapToStatusDto(article.getArticleStatus())
        );
    }

    private AuthorDto mapToAuthorDto(User author) {
        // CAMBIO: Usamos los getters correctos y añadimos los nuevos campos
        return new AuthorDto(
                author.getUserId(),     // <-- .getUserId()
                author.getUsername(),
                author.getFirstName(),  // <-- Nuevo
                author.getLastName()    // <-- Nuevo
        );
    }

    private ArticleStatusDto mapToStatusDto(ArticleStatus status) {
        // (Sin cambios)
        return new ArticleStatusDto(
                status.getIdArticleStatus(),
                status.getStatusName()
        );
    }
}