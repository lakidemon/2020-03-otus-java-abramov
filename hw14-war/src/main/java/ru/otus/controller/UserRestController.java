package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.core.service.DBServiceUser;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRestController {
    private final DBServiceUser userService;
}
