package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.TermsImportPayload.TermsImportPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.service.DTermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import/terms")
@CrossOrigin(origins = "http://localhost:8080")
public class MyDTermsImportRestController {

    private final DTermService draftTermService;

    @PostMapping()
    public List<ImportTermDto> prepare(@RequestBody @Valid TermsImportPayload payload) {
        return this.draftTermService.prepareImport(
                payload.text(), payload.colSeparator(), payload.rowSeparator(), payload.colCustom(), payload.rowCustom());
    }
}
