package ru.otus.jdbc.dao;

import lombok.Getter;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.User;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.jdbc.DbExecutor;
import ru.otus.jdbc.mapper.BasicJdbcMapper;
import ru.otus.jdbc.mapper.JdbcMapper;

import java.util.Optional;

public class UserDaoJdbc implements UserDao {
    @Getter
    private final SessionManager sessionManager;
    private final JdbcMapper<User> mapper;

    public UserDaoJdbc(SessionManager sessionManager, DbExecutor<User> dbExecutor) {
        this.sessionManager = sessionManager;
        this.mapper = BasicJdbcMapper.forType(User.class, sessionManager, dbExecutor);
    }

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
