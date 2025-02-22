package dev.braindeck.api.repository;

import dev.braindeck.api.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguagesRepository extends JpaRepository<LanguageEntity, Integer>
{

}
