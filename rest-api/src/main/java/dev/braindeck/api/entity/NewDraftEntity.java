package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="t_drafts")
public class NewDraftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private UserEntity user;

    public NewDraftEntity(UserEntity user) {
        this.user = user;
    }
}
