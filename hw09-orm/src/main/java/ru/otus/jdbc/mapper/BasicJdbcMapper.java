package ru.otus.jdbc.mapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.jdbc.DbExecutor;
import ru.otus.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.metadata.EntitySQLMetaDataImpl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BasicJdbcMapper<T> implements JdbcMapper<T> {
    @Getter
    private final Class<T> type;
    private final SessionManager sessionManager;
    private final DbExecutor<T> dbExecutor;
    private final EntityClassMetaData<T> classMetaData;
    private final EntitySQLMetaData sqlMetaData;

    @Override
    public void insert(@NonNull T objectData) {
        try {
            dbExecutor.executeInsert(sessionManager.getCurrentSession().getConnection(), sqlMetaData.getInsertSql(),
                    extractFields(objectData, classMetaData.getFieldsWithoutId()));
        } catch (SQLException e) {
            throw new MapperException("Cannot execute insert for " + objectData, e);
        }
    }

    @Override
    public void update(@NonNull T objectData) {

    }

    @Override
    public void insertOrUpdate(@NonNull T objectData) {

    }

    @Override
    public Optional<T> findById(long id) {
        try {
            return dbExecutor.executeSelect(sessionManager.getCurrentSession().getConnection(),
                    sqlMetaData.getSelectByIdSql(), id, this::mapObject);
        } catch (SQLException e) {
            throw new MapperException("Cannot execute select for id " + id, e);
        }
    }

    private T mapObject(ResultSet resultSet) {
        var constructorParams = classMetaData.getAllFields().stream().map(f -> {
            try {
                return resultSet.getObject(f.getName());
            } catch (SQLException e) {
                throw new MapperException("Cannot get column " + f.getName() + " value from ResultSet", e);
            }
        }).toArray(Object[]::new);

        try {
            return classMetaData.getConstructor().newInstance(constructorParams);
        } catch (ReflectiveOperationException e) {
            throw new MapperException("Failed to instantiate object " + type.getName(), e);
        }
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

    private List<Object> extractFields(T object, List<Field> fields) {
        return fields.stream().map(f -> {
            try {
                return f.get(object);
            } catch (IllegalAccessException e) {
                throw new MapperException("Cannot get value of " + f.getName() + " field", e);
            }
        }).collect(Collectors.toList());
    }

}
