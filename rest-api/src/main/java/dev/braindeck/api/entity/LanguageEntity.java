package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(schema="braindeck", name="t_languages")
public class LanguageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", length =50, nullable = false, unique = false)
    private String name;

    @Column(name="top", nullable = true, unique = false)
    private Boolean top;
}
