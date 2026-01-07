package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="t_drafts")
public class DraftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="title", length =50, nullable = false, unique = false)
    private String title;

    @Column(name="description", length =500, nullable = false, unique = false)
    private String description;

    @Column(name="term_language_id", nullable = false, unique = false)
    private Integer termLanguageId;

    @Column(name="description_language_id", nullable = false, unique = false)
    private Integer descriptionLanguageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "draft", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DTermEntity> terms = new ArrayList<>();

    @Column (name="created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public DraftEntity(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, UserEntity user) {
        this.title = title;
        this.description = description;
        this.termLanguageId = termLanguageId;
        this.descriptionLanguageId = descriptionLanguageId;
        this.user = user;
    }



}
