package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Tuple;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    @Query("""
        SELECT new dev.braindeck.api.dto.SetWithTermCountDto(
            s.id, s.title, s.description, s.termLanguageId, s.descriptionLanguageId, s.updatedAt, COUNT(t)
        ) FROM SetEntity s LEFT JOIN s.terms t WHERE s.user.id = :userId GROUP BY s.id
    """)
    List<SetWithTermCountDto> findAllByUser(@Param("userId") int userId);

}
