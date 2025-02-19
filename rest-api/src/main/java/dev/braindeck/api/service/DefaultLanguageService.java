package dev.braindeck.api.service;

import dev.braindeck.api.entity.Language;
import dev.braindeck.api.repository.LanguagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultLanguageService implements LanguageService {

    private final LanguagesRepository languagesRepository;


    @Override
    public Map<String, Map<Integer, String>> findAllByType() {
        List<Language> languagesList = this.languagesRepository.findAll();
        Map<String, Map<Integer, String>> languagesByType = new HashMap<>();
        languagesByType.put("my", new HashMap<Integer, String>());
        languagesByType.put("top", new HashMap<Integer, String>());
        languagesByType.put("rest", new HashMap<Integer, String>());

        for (Language language : languagesList) {
            if (language.getTop() == Boolean.TRUE) {
                languagesByType.get("top").put(language.getId(), language.getName());
            } else {
                languagesByType.get("rest").put(language.getId(), language.getName());
            }
        }
        return languagesByType;
//        return Map.of(
//                "all", this.languageService.findAll(),
//                "my", this.languageService.getMy(),
//                "top", this.languageService.getTop(),
//                "rest", this.languageService.getRest()
//        );
    }
}
