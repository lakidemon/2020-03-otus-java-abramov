package ru.otus.jdbc.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
public class JdbcMapperImpl<T> implements JdbcMapper<T> {
    private final SessionManager sessionManager;
    private final DbExecutor<T> dbExecutor;
    private final EntityClassMetaData<T> classMetaData;
    private final EntitySQLMetaData sqlMetaData;

    @Override
    public void insert(@NonNull T objectData) {
        try {
            var id = dbExecutor.executeInsert(sessionManager.getCurrentSession().getConnection(),
                    sqlMetaData.getInsertSql(), extractFields(objectData, classMetaData.getFieldsWithoutId()));
            injectId(objectData, id);
        } catch (SQLException e) {
            throw new MapperException("Cannot execute insert for " + objectData, e);
        }
    }

    @Override
    public void update(@NonNull T objectData) {
        var id = extractId(objectData);
        if (id == 0) {
            throw new MapperException("Cannot update object " + objectData + " which is not stored in database");
        }
        try {
            var params = extractFields(objectData, classMetaData.getFieldsWithoutId());
            params.add(id);
            dbExecutor.executeInsert(sessionManager.getCurrentSession().getConnection(), sqlMetaData.getUpdateSql(),
                    params);
        } catch (SQLException e) {
            throw new MapperException("Cannot update " + objectData, e);
        }
    }

    @Override
    public void insertOrUpdate(@NonNull T objectData) {
        if (extractId(objectData) == 0) {
            insert(objectData);
        } else {
            update(objectData);
        }
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
        try {
            if (!resultSet.next()) {
                return null;
            }
        } catch (SQLException e) {
            throw new MapperException("Unexpected exception during mapping", e);
        }

        try {
            var object = classMetaData.getConstructor().newInstance();

            classMetaData.getAllFields().stream().forEach(f -> {
                try {
                    var value = resultSet.getObject(f.getName());
                    f.set(object, value);
                } catch (SQLException e) {
                    throw new MapperException("Cannot get column " + f.getName() + " value from ResultSet", e);
                } catch (IllegalAccessException e) {
                    throw new MapperException("Cannot set field " + f.getName() + " of type " + object.getClass(), e);
                }
            });
            return object;
        } catch (ReflectiveOperationException e) {
            throw new MapperException("Failed to instantiate object", e);
        }
    }

    @SneakyThrows
    private void injectId(T object, long id) {
        classMetaData.getIdField().set(object, id);
    }

    @SneakyThrows
    private int extractId(T object) {
        return ((Number) classMetaData.getIdField().get(object)).intValue();
    }

    public static <T> JdbcMapperImpl<T> forType(Class<T> type, SessionManager manager, DbExecutor<T> executor) {
        var classMetaData = EntityClassMetaDataImpl.create(type);
        var sqlMetaData = EntitySQLMetaDataImpl.createFromClass(classMetaData);
        var mapper = new JdbcMapperImpl<>(manager, executor, classMetaData, sqlMetaData);

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
