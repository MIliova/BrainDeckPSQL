package dev.braindeck.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema="braindeck", name="t_sets")
public class SetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @Size(min = 1, max = 500)
    private String description;

    @NotNull
    private Integer termLanguageId;

    @NotNull
    private Integer descriptionLanguageId;

    @ManyToOne
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private User user;

    @OneToMany(mappedBy = "set", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TermEntity> terms = new ArrayList<>();
}
