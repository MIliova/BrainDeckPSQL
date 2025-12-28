package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.TermsImportPayload.TermsImportPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.service.DTermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import/terms")
public class TermsImportRestController {

    private final DTermService draftTermService;

    @PostMapping("/preview")
    public ResponseEntity<List<ImportTermDto>> prepare(
            @RequestBody @Valid TermsImportPayload payload) {
        return ResponseEntity.ok(draftTermService.prepareImport(
                payload.text(), payload.colSeparator(), payload.rowSeparator(), payload.colCustom(), payload.rowCustom()));
    }
}
