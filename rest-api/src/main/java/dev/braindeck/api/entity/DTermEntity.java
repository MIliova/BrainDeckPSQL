package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "draft")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="t_drafts_terms")
public class DTermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="term", length=950, nullable = false, unique = false)
    private String term;

    @Column(name="description", length=950, nullable = true, unique = false)
    private String description;

    @ManyToOne
    @JoinColumn(name="draft_id", updatable = false, nullable = false)
    private DraftSetEntity draft;

}
