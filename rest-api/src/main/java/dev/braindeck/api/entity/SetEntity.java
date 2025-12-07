package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "terms"})
@Entity
@Table(name="t_sets")
public class SetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "set_seq")
    @SequenceGenerator(name = "set_seq", sequenceName = "set_seq", allocationSize = 50)
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

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TermEntity> terms = new ArrayList<>();

    @Column (name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column (name="updated_at", nullable = false)
    private Instant updatedAt;

    public SetEntity(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, UserEntity user) {
        this.title = title;
        this.description = description;
        this.termLanguageId = termLanguageId;
        this.descriptionLanguageId = descriptionLanguageId;
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
