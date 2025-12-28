package dev.braindeck.api.controller;

import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.service.SetService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SetRestController {

    private final SetService setService;

    @GetMapping("/users/{userId:\\d+}/sets")
    public ResponseEntity<List<SetWithTermCountDto>> findSets(
            @PathVariable @Positive int userId) {
        return ResponseEntity.ok(setService.findAllByUserId(userId));
    }

    @GetMapping("/sets/{setId:\\d+}")
    public ResponseEntity<SetDto> findSet(
            @PathVariable @Positive int setId) {
        return ResponseEntity.ok(setService.findById(setId));
    }

}
