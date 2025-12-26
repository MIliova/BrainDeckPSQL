package dev.braindeck.web.client;

import dev.braindeck.web.entity.LanguagesDto;


public interface LanguagesRestClient {
    LanguagesDto findAllByTypes();
}
