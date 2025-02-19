package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    List<SetEntity> findAllByUserId(Integer userId);

}
