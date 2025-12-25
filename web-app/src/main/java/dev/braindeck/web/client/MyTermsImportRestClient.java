package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;

import java.util.List;

public interface MyTermsImportRestClient {

    List<NewTermDto> prepare(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

}
