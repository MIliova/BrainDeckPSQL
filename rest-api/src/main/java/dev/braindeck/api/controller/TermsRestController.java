package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.TermsImportPayload.TermsImportPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.service.TermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class TermsRestController {

    private final TermService termService;

    @PostMapping("/terms/prepare-import")
    public List<ImportTermDto> prepareImport(@RequestBody @Valid TermsImportPayload payload, BindingResult bindingResult) throws BindException {

        System.out.println(payload);

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            return this.termService.prepareImport(
                    payload.text(), payload.colSeparator(), payload.rowSeparator(), payload.colCustom(), payload.rowCustom());
        }
    }
}
