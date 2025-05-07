package dev.braindeck.api.repository;

import dev.braindeck.api.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Integer> {
    List<FolderEntity> findAllByUserId(Integer userId);
}
