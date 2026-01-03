package dev.braindeck.api.service;

import dev.braindeck.api.dto.LanguagesDtoDeprecated;
import org.springframework.stereotype.Service;

@Service
public interface LanguageService {

    LanguagesDtoDeprecated findAllByType();

}
