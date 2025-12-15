package dev.braindeck.api.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface LanguageService {

    Map<String, Map<Integer, String>> findAllByType();

}
