package dev.braindeck.web.entity;

import java.util.List;

public record SetOfTerms (
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        List<Term> terms
) {

}
