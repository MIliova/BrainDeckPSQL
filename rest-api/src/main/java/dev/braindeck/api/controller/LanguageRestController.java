package dev.braindeck.api.controller;

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
    public Map<String, Map<Integer, String>> findAllByType() {
        return this.languageService.findAllByType();
    }

//    @GetMapping("/api/languages/{setId:\\d+}")
//    public String getById(@PathVariable final int setId) {
//        return this.languagesService.getById(setId);
//    }

}
