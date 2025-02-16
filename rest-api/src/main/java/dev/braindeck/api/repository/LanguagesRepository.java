package dev.braindeck.api.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface LanguagesRepository
{
    Map<Integer,String> getAll();
    Map<Integer,String> getMy();
    Map<Integer,String> getTop();
    Map<Integer,String> getRest();

    String getById(int id);
}
