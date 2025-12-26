package dev.braindeck.web.service;

import dev.braindeck.web.controller.FieldErrorDto;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.UpdateSetPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;

import java.util.List;
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

    public static SetFormResult error(List<FieldErrorDto> errors,
                                      NewSetPayload payload,
                                      List<NewTermPayload> terms) {
        return new SetFormResult(null, Map.of(
                "errors", errors,
                "payload", payload,
                "terms", terms
        ));
    }

    public static SetFormResult error(List<FieldErrorDto> errors,
                                      UpdateSetPayload payload,
                                      List<UpdateTermPayload> terms) {
        return new SetFormResult(null, Map.of(
                "errors", errors,
                "payload", payload,
                "terms", terms
        ));
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