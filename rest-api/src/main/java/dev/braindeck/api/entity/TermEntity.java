package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString(exclude = "set")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema="braindeck", name="t_terms")
public class TermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="term", length=950, nullable = false, unique = false)
    private String term;

    @Column(name="description", length=950, nullable = true, unique = false)
    private String description;

    @ManyToOne
    @JoinColumn(name="set_id", updatable = false, nullable = false)
    private SetEntity set;

}
