package dev.braindeck.api.service;

import dev.braindeck.api.repository.LanguagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultLanguagesService implements LanguagesService {

    private final LanguagesRepository languagesRepository;

    @Override
    public Map<Integer, String> getAll() {
        return this.languagesRepository.getAll();
    }

    @Override
    public Map<Integer, String> getMy() {
        return this.languagesRepository.getMy();
    }

    @Override
    public Map<Integer, String> getTop() {
        return this.languagesRepository.getTop();
    }

    @Override
    public Map<Integer, String> getRest() {
        return this.languagesRepository.getRest();
    }

    @Override
    public String getById(Integer id) {
        return this.languagesRepository.getById(id);
    }
}
