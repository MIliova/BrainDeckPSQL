package dev.braindeck.api.controller;

import dev.braindeck.api.dto.SetWithTCntUInfoDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/sets")
public class MySetsRestController {

    private final SetService setService;
    private final UserService userService;


    @GetMapping("")
    public ResponseEntity<List<SetWithTCntUInfoDto>> findSets() {
        UserEntity user = userService.getCurrentUser();

        return ResponseEntity.ok(setService.findAllByUserId(user.getId()));
    }



}
