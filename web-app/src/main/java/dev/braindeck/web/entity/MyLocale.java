package dev.braindeck.web.entity;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MyLocale {
    private Map<String, String> availables;

    public MyLocale() {
        availables = new HashMap<>();
        availables.put("en", "EN");
        availables.put("de", "DE");
        availables.put("ru", "RU");
    }
}