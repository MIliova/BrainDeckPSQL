package dev.braindeck.web.controller;

import dev.braindeck.web.client.UserRestClient;
import dev.braindeck.web.entity.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRestClient userRestClient;

    @ModelAttribute("user")
    public UserDto findCurrentUser() {
        System.out.println("findCurrentUser");
        return userRestClient.get();
    }

    @GetMapping("")
    public String getMain(@ModelAttribute("user") UserDto user) {
        if (user != null) {
            return "redirect:/user/"+user.id()+"/sets";
        } else {
            return "error/404";
        }
    }

}
