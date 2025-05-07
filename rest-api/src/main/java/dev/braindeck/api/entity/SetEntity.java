package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="t_sets")
public class SetEntity {

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "set", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TermEntity> terms = new ArrayList<>();

}
