package dev.braindeck.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema="braindeck.t_terms")
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="set_id", updatable = false, nullable = false)
    private Set set;

    @NotNull
    @Size(min = 1, max = 950)
    private String term;

    @Size(min = 1, max = 950)
    private String description;




}
