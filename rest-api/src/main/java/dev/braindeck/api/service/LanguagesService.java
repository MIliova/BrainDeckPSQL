package dev.braindeck.api.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface LanguagesService {

    Map<Integer, String> findAll();
    Map<Integer, String> getMy();
    Map<Integer, String> getTop();
    Map<Integer, String> getRest();
}
