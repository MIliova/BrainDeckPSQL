package dev.braindeck.api.controller;

import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.service.SetService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SetRestController {

    private final SetService setService;

    @GetMapping("/users/{userId:\\d+}/sets")
    public List<SetWithTermCountDto> findSets(
            @PathVariable @Positive int userId) {
        return setService.findAllByUserId(userId);
    }

    @GetMapping("/sets/{setId:\\d+}")
    public SetDto findSet(
            @PathVariable @Positive int setId) {
        return setService.findById(setId);
    }

}
