package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    List<SetEntity> findAllByUserId(Integer userId);

//    @EntityGraph(attributePaths = {"terms","user"})
//    Optional<SetEntity> findByIdWithTerms(Integer id);

    @Query("SELECT s FROM SetEntity s LEFT JOIN FETCH s.terms WHERE s.id = :id")
    Optional<SetEntity> findByIdWithTerms(@Param("id") Integer id);

    @Query("SELECT s, COUNT(t) AS termCount FROM SetEntity s LEFT JOIN s.terms t WHERE s.user.id = :userId GROUP BY s.id")
    List<Tuple> findAllByUserIdWithTermCount(@Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM SetEntity s WHERE s.id = :id")
    void deleteById(@Param("id") Integer id);

}
