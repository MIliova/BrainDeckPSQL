package dev.braindeck.web.service;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class SetFormResult {

    private final String redirectUrl;
    private final Map<String, Object> modelAttributes;

    private SetFormResult(String redirectUrl, Map<String, Object> modelAttributes) {
        this.redirectUrl = redirectUrl;
        this.modelAttributes = modelAttributes;
    }

    public static SetFormResult redirect(String url) {
        return new SetFormResult(url, null);
    }

    public static SetFormResult error(Map<String, Object> attrs) {
        return new SetFormResult(null, attrs);
    }

    public boolean isRedirect() {
        return redirectUrl != null;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Map<String, Object> getModelAttributes() {
        return modelAttributes;
    }
}


