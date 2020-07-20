package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.model.Address;
import ru.otus.model.Phone;
import ru.otus.model.User;

import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DefaultUserPopulatorService implements UserPopulatorService {
    private final DBServiceUser userService;

    @Override
    public void populate(DBServiceUser userService) {
        var random = ThreadLocalRandom.current();
        for (String name : new String[] { "Вася", "Петя", "Катя", "Володя", "Иннокентий", "Авдотья" }) {
            var addresses = TimeZone.getAvailableIDs();
            var phones = IntStream.range(0, random.nextInt(1, 4))
                    .mapToObj(i -> String.valueOf(random.nextLong(89000000000L, 89500000000L)))
                    .map(Phone::new)
                    .collect(Collectors.toList());
            var user = new User(name, random.nextInt(12, 70), new Address(addresses[random.nextInt(addresses.length)]),
                    phones);
            userService.saveUser(user);
        }
    }

    @Override
    public void afterPropertiesSet() {
        populate(userService);
    }
}
