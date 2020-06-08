package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.jdbc.mapper.metadata.EntitySQLMetaDataImpl;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EntitySQLMetaDataTest {
    private static EntitySQLMetaData sqlMetaData;

    @BeforeAll
    static void setup() {
        sqlMetaData = EntitySQLMetaDataImpl.create("User", "id", Arrays.asList("name", "age"));
    }

    @Test
    void shouldGenerateCorrectSelectAll() {
        assertEquals("SELECT * FROM User ;", sqlMetaData.getSelectAllSql());
    }

    @Test
    void shouldGenerateCorrectSelectById() {
        assertEquals("SELECT * FROM User WHERE `id`=?;", sqlMetaData.getSelectByIdSql());
    }

    @Test
    void shouldGenerateCorrectInsert() {
        assertEquals("INSERT INTO User (name,age) VALUES (?,?);", sqlMetaData.getInsertSql());
    }

    @Test
    void shouldGenerateCorrectUpdate() {
        assertEquals("UPDATE User SET `name`=?,`age`=? WHERE `id`=?;", sqlMetaData.getUpdateSql());
    }
}