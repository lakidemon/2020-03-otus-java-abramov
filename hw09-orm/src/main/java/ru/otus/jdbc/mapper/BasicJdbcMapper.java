package ru.otus.jdbc.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutor;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.metadata.EntitySQLMetaDataImpl;
import ru.otus.jdbc.sessionmanager.SessionManagerJdbc;

@RequiredArgsConstructor
@Slf4j
public class BasicJdbcMapper<T> implements JdbcMapper<T> {
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

    public static <T> BasicJdbcMapper<T> forType(Class<T> type) {
        return forType(type, new SessionManagerJdbc(new DataSourceH2()), new DbExecutorImpl<>());
    }

    public static <T> BasicJdbcMapper<T> forType(Class<T> type, SessionManager manager, DbExecutor<T> executor) {
        var classMetaData = EntityClassMetaDataImpl.create(type);
        var sqlMetaData = EntitySQLMetaDataImpl.createFromClass(classMetaData);
        var mapper = new BasicJdbcMapper<>(type, manager, executor, classMetaData, sqlMetaData);

        log.debug("Name: " + classMetaData.getName());
        log.debug("Select all: " + sqlMetaData.getSelectAllSql());
        log.debug("Select by id: " + sqlMetaData.getSelectByIdSql());
        log.debug("Insert: " + sqlMetaData.getInsertSql());
        log.debug("Update: " + sqlMetaData.getUpdateSql());

        return mapper;
    }

}
