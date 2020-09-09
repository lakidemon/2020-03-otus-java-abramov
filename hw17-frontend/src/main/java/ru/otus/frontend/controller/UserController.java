package ru.otus.frontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/users")
    public String users() {
        return "users";
    }
}
