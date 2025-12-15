package dev.braindeck.api.service;

import dev.braindeck.api.dto.LanguagesDto;
import org.springframework.stereotype.Service;

@Service
public interface LanguageService {

    LanguagesDto findAllByType();

}
