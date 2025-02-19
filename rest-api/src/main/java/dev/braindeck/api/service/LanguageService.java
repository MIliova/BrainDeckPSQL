package dev.braindeck.api.service;

import dev.braindeck.api.entity.Language;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public interface LanguageService {

    Map<String, Map<Integer, String>> findAllByType();
//    Map<Integer, String> getMy();
//    Map<Integer, String> getTop();
//    Map<Integer, String> getRest();
}
