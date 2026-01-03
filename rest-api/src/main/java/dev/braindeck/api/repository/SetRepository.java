package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.SetWithTermCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    @Query("""
        SELECT new dev.braindeck.api.dto.SetWithTermCountDto(
            s.id, s.title, s.description, s.updatedAt, COUNT(t)
        ) FROM SetEntity s LEFT JOIN s.terms t WHERE s.user.id = :userId GROUP BY s.id
    """)
    List<SetWithTermCountDto> findAllByUser(@Param("userId") int userId);

    @Query("""
        SELECT s FROM SetEntity s WHERE s.id = :setId AND s.user.id = :userId
    """)
    Optional<SetEntity> findBySetIdAndUserId(@Param("setId") int setId, @Param("userId") int userId);
}
