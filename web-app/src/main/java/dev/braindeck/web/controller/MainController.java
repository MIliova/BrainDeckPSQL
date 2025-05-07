package dev.braindeck.web.controller;

import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.entity.SetWithCountDto;
import dev.braindeck.web.entity.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final SetsRestClient setsRestClient;

    @ModelAttribute("user")
    public UserDto findCurrentUser() {
        System.out.println("findCurrentUser");
        return this.setsRestClient.findCurrentUser();
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
