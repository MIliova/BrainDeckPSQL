package dev.braindeck.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//not used
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_draft_sets", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class OneToOneTest {
    @Id
    @Column(name = "user_id")
    private Integer id;

    @jakarta.persistence.OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @jakarta.persistence.OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private SetEntity set;

}

