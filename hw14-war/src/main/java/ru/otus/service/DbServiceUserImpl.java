package ru.otus.service;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.dao.UserDao;
import ru.otus.model.Address;
import ru.otus.model.Phone;
import ru.otus.model.User;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
public class DbServiceUserImpl implements DBServiceUser {
    private static final Logger logger = LoggerFactory.getLogger(DbServiceUserImpl.class);

    private final UserDao userDao;

    @Override
    public long saveUser(User user) {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                var userId = userDao.insertUser(user);
                sessionManager.commitSession();

                logger.info("created user: {}", userId);
                return userId;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }

    @Override
    public Optional<User> getUser(long id) {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                Optional<User> userOptional = userDao.findById(id);

                logger.info("user: {}", userOptional.orElse(null));
                return userOptional;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }

    @Override
    public void updateUser(User user) {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                userDao.updateUser(user);
                sessionManager.commitSession();

                logger.info("updated user: {}", user.getId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }

    @Override
    public Collection<User> getAll() {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            return ImmutableList.copyOf(userDao.findAll());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DbServiceException(e);
        }
    }

    @Override
    public Optional<User> getRandom() {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return userDao.findAny();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }

    @PostConstruct
    private void addTestUsers() {
        var random = ThreadLocalRandom.current();
        for (String name : new String[] { "Вася", "Петя", "Катя", "Володя", "Иннокентий", "Авдотья" }) {
            var addresses = TimeZone.getAvailableIDs();
            var phones = IntStream.range(0, random.nextInt(1, 4))
                    .mapToObj(i -> String.valueOf(random.nextLong(89000000000L, 89500000000L)))
                    .map(Phone::new)
                    .collect(Collectors.toList());
            var user = new User(name, random.nextInt(12, 70), new Address(addresses[random.nextInt(addresses.length)]),
                    phones);
            saveUser(user);
        }
    }
}
