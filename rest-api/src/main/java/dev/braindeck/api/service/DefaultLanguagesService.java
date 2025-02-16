package dev.braindeck.api.service;

import dev.braindeck.api.entity.Language;
import dev.braindeck.api.repository.LanguagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultLanguagesService implements LanguagesService {

    private final LanguagesRepository languagesRepository;

    @Override
    public Map<Integer, String> findAll() {
        return this.languagesRepository.findAll().stream().collect(Collectors.toMap(Language::getId, Language::getName));
    }

    @Override
    public Map<Integer, String> getMy() {
        return new HashMap<>();
    }

    @Override
    public Map<Integer, String> getTop() {
        return this.languagesRepository.findAll().stream()
                .filter(language -> language.getTop() == Boolean.TRUE)
                .collect(Collectors.toMap(Language::getId, Language::getName));
    }

    @Override
    public Map<Integer, String> getRest() {
        return this.languagesRepository.findAll().stream()
                .filter(language -> language.getTop() != Boolean.TRUE)
                .collect(Collectors.toMap(Language::getId, Language::getName));
    }

}
