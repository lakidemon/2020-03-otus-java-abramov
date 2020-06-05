package ru.otus.jdbc.mapper.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

@RequiredArgsConstructor
@Getter
public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public static EntitySQLMetaData createFromClass(EntityClassMetaData classMetaData) {

        return new EntitySQLMetaDataImpl(null, null, null, null);
    }
}
