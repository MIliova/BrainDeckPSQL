package dev.braindeck.web.client;

import java.util.Map;

public interface LanguagesRestClient {
    Map<String, Map<Integer, String>> findAllByTypes();
}
