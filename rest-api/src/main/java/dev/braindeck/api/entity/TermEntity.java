package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "set")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="t_terms")
public class TermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "term_seq")
    @SequenceGenerator(name = "term_seq", sequenceName = "term_seq", allocationSize=50)
    private Integer id;

    @Column(name="term", length=950, nullable = false, unique = false)
    private String term;

    @Column(name="description", length=950, nullable = true, unique = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="set_id", updatable = false, nullable = false)
    private SetEntity set;

    public TermEntity(String term, String description, SetEntity set) {
        this.term = term;
        this.description = description;
        this.set = set;
    }


}
