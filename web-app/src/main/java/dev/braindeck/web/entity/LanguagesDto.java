package dev.braindeck.web.entity;

import java.util.Map;

public record LanguagesDto(
        Map<Integer, String> top,
        Map<Integer, String> rest,
        Map<Integer, String> my
) {}
