package ru.otus.jdbc.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.User;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.jdbc.mapper.JdbcMapper;

import java.util.Optional;

@RequiredArgsConstructor
public class UserDaoJdbc implements UserDao {
    @Getter
    private final SessionManager sessionManager;
    private final JdbcMapper<User> mapper;

    @Override
    public Optional<User> findById(long id) {
        return mapper.findById(id);
    }

    @Override
    public long insertUser(User user) {
        mapper.insert(user);
        return user.getId();
    }

    @Override
    public void updateUser(User user) {
        mapper.update(user);
    }

    @Override
    public void insertOrUpdate(User user) {
        mapper.insertOrUpdate(user);
    }

}
