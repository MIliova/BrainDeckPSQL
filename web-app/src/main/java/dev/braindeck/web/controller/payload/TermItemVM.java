package dev.braindeck.web.controller.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TermItemVM<T> {

    private T payload;

    private boolean hasErrors;

    private Map<String, String> errors = new HashMap<>();

    public TermItemVM(T payload) {
        this.payload = payload;
    }
}
