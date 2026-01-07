package dev.braindeck.api.repository;

import dev.braindeck.api.dto.SetShortDto;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.SetWithTCntUInfoSqlDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    @Query("""
        SELECT new dev.braindeck.api.dto.SetWithTCntUInfoSqlDto(
            s.id, 
            s.title, 
            s.description, 
            s.updatedAt, 
            COUNT(t),
            s.user.id,
            s.user.name
        ) 
            FROM SetEntity s 
            LEFT JOIN s.terms t 
            WHERE s.user.id = :userId 
            GROUP BY s.id, s.user.id, s.user.name
    """)
    List<SetWithTCntUInfoSqlDto> findAllByUser(@Param("userId") int userId);

    @Query("""
    SELECT new dev.braindeck.api.dto.SetShortDto(
        s.id,
        s.title,
        s.description,
        s.user.id,
        s.user.name,
        null
    )
    FROM SetEntity s
    WHERE s.id = :id
""")
    Optional<SetShortDto> findDtoById(@Param("id") int id);

    @Query("""
        SELECT s FROM SetEntity s WHERE s.id = :setId AND s.user.id = :userId
    """)
    Optional<SetEntity> findBySetIdAndUserId(@Param("setId") int setId, @Param("userId") int userId);
}
