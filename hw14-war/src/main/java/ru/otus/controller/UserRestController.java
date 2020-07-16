package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.User;
import ru.otus.service.DBServiceUser;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRestController {
    private final DBServiceUser userService;

    @PostMapping("/api/users")
    public ResponseEntity addUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        return ResponseEntity.of(userService.getUser(id));
    }

    @GetMapping("/api/users/random")
    public ResponseEntity<User> getRandomUser() {
        return ResponseEntity.of(userService.getRandom());
    }
}
