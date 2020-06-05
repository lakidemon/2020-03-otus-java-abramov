package ru.otus.jdbc.dao;

import lombok.RequiredArgsConstructor;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutor;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;
import ru.otus.jdbc.mapper.JdbcMapper;
import ru.otus.jdbc.sessionmanager.SessionManagerJdbc;

@RequiredArgsConstructor
public class BasicDao<T> implements JdbcMapper<T> {
    private final Class<T> type;
    private final SessionManager sessionManager;
    private final DbExecutor<T> dbExecutor;
    private final EntityClassMetaData<T> classMetaData;
    private final EntitySQLMetaData sqlMetaData;

    @Override
    public void insert(T objectData) {

    }

    @Override
    public void update(T objectData) {

    }

    @Override
    public void insertOrUpdate(T objectData) {

    }

    @Override
    public T findById(long id, Class<T> clazz) {
        return null;
    }

    public static <T> BasicDao<T> forType(Class<T> type) {

        return new BasicDao<T>(type, new SessionManagerJdbc(new DataSourceH2()), new DbExecutorImpl<T>(), null, null);
    }
}
