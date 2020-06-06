package ru.otus.jdbc.mapper.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Getter
public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private static final String SELECT_TEMPLATE = "SELECT * FROM %s %s;";
    private static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s;";

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public static EntitySQLMetaData createFromClass(EntityClassMetaData<?> classMetaData) {
        var tableName = classMetaData.getName();
        var whereId = String.format("WHERE `%s`=?", classMetaData.getIdField().getName());

        var selectAll = String.format(SELECT_TEMPLATE, tableName, "");
        var selectById = String.format(SELECT_TEMPLATE, tableName, whereId);
        var insert = String.format(INSERT_TEMPLATE, tableName, concatFieldNames(classMetaData.getFieldsWithoutId()),
                generateParameters(classMetaData.getFieldsWithoutId().size()));
        var update = String.format(UPDATE_TEMPLATE, tableName,
                generateFieldNamesAndParameters(classMetaData.getFieldsWithoutId()), whereId);

        return new EntitySQLMetaDataImpl(selectAll, selectById, insert, update);
    }

    private static String generateFieldNamesAndParameters(List<Field> fields) {
        return fields.stream().map(field -> String.format("`%s`=?", field.getName())).collect(Collectors.joining(","));
    }

    private static String generateParameters(int amount) {
        return IntStream.range(0, amount).mapToObj(i -> "?").collect(Collectors.joining(","));
    }

    private static String concatFieldNames(List<Field> fields) {
        return fields.stream().map(Field::getName).collect(Collectors.joining(","));
    }
}
