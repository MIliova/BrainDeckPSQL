package dev.braindeck.api.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTerm {
    private String term;
    private String description;

    @ManyToOne
    @JoinColumn(name="set_id", updatable = false, nullable = false)
    private Set set;
}
