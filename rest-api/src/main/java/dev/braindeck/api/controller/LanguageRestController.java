package dev.braindeck.api.controller;

import dev.braindeck.api.dto.LanguagesDtoDeprecated;
import dev.braindeck.api.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LanguageRestController {

    private final LanguageService languageService;

    @GetMapping("/api/languages")
    public ResponseEntity<LanguagesDtoDeprecated> findAllByType() {
        return ResponseEntity.ok(languageService.findAllByType());
    }

}
