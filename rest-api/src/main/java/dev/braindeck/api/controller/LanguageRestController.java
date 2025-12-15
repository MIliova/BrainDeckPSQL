package dev.braindeck.api.controller;

import dev.braindeck.api.domain.LanguageType;
import dev.braindeck.api.dto.LanguagesDto;
import dev.braindeck.api.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LanguageRestController {

    private final LanguageService languageService;

    @GetMapping("/api/languages")
    public LanguagesDto findAllByType() {
        return languageService.findAllByType();
    }

}
