package ru.otus.database.dao;

import ru.otus.core.model.User;
import ru.otus.database.sessionmanager.SessionManager;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);

    long insertUser(User user);

    void updateUser(User user);

    void insertOrUpdate(User user);

    Collection<User> findAll();

    Optional<User> findAny();

    SessionManager getSessionManager();
}
