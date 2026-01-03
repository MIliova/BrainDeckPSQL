package dev.braindeck.api.service;

import dev.braindeck.api.domain.LanguageType;
import dev.braindeck.api.dto.LanguagesDtoDeprecated;
import dev.braindeck.api.entity.LanguageEntity;
import dev.braindeck.api.repository.LanguagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguagesRepository languagesRepository;

    @Override
    public LanguagesDtoDeprecated findAllByType() {
        Map<LanguageType, Map<Integer, String>> result = new EnumMap<>(LanguageType.class);
        result.put(LanguageType.MY, new HashMap<>());
        result.put(LanguageType.TOP, new HashMap<>());
        result.put(LanguageType.REST, new HashMap<>());

        for (LanguageEntity language : languagesRepository.findAll()) {
            LanguageType type = Boolean.TRUE.equals(language.getTop()) ? LanguageType.TOP : LanguageType.REST;
            result.get(type).put(language.getId(), language.getName());
        }

        return new LanguagesDtoDeprecated(
                result.get(LanguageType.TOP),
                result.get(LanguageType.REST),
                result.get(LanguageType.MY)
        );

    }
}
