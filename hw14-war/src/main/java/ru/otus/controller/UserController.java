package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.core.service.DBServiceUser;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final DBServiceUser userService;

    @GetMapping("/users")
    public String users(ModelAndView mv) {

        return "users";
    }
}
