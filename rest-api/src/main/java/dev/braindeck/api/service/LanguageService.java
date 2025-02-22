package dev.braindeck.api.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface LanguageService {

    Map<String, Map<Integer, String>> findAllByType();
//    Map<Integer, String> getMy();
//    Map<Integer, String> getTop();
//    Map<Integer, String> getRest();
}
