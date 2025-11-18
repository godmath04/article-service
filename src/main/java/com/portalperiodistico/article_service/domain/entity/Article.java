package com.portalperiodistico.article_service.domain.entity;

import com.portalperiodistico.auth_service.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "[ARTICLE]")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[IdArticle]")
    private Long idArticle;

    // ... (title, content, createdAt, updatedAt sin cambios) ...
    @Column(name = "[Title]", nullable = false, length = 255)
    private String title;

    @Column(name = "[Content]", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "[CreatedAt]", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "[UpdatedAt]")
    private LocalDateTime updatedAt;

    // --- Relación con User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "[IdUser]", referencedColumnName = "UserID", nullable = false)
    private User author;

    // --- Relación con ArticleStatus (Sin cambios) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "[IdArticleStatus]", referencedColumnName = "[IdArticleStatus]", nullable = false)
    private ArticleStatus articleStatus;
}