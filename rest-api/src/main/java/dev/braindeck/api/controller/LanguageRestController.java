package dev.braindeck.api.controller;

import dev.braindeck.api.service.LanguagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LanguageRestController {

    private final LanguagesService languagesService;

    @GetMapping("/api/languages")
    public Map<String, Map<Integer, String>> getAll() {
        return Map.of(
                "all", this.languagesService.getAll(),
                "my", this.languagesService.getMy(),
                "top", this.languagesService.getTop(),
                "rest", this.languagesService.getRest()
        );
    }

//    @GetMapping("/api/languages/{setId:\\d+}")
//    public String getById(@PathVariable final int setId) {
//        return this.languagesService.getById(setId);
//    }

}
