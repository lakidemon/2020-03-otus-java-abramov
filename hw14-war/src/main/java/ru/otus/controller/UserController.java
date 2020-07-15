package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;

import java.util.Collection;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final DBServiceUser userService;

    @GetMapping("/users")
    public String users() {
        return "users";
    }

    @ModelAttribute("allUsers")
    public Collection<User> populateUsers() {
        return userService.getAll();
    }

}
