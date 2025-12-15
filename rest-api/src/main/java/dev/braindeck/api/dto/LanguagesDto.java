package dev.braindeck.api.dto;

import java.util.Map;

public record LanguagesDto(
        Map<Integer, String> top,
        Map<Integer, String> rest,
        Map<Integer, String> my
) {}
