package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("должен корректно создавать SELECT * запрос")
    void shouldGenerateCorrectSelectAll() {
        assertEquals("SELECT * FROM User ;", sqlMetaData.getSelectAllSql());
    }

    @Test
    @DisplayName("должен корректно создавать SELECT by id запрос")
    void shouldGenerateCorrectSelectById() {
        assertEquals("SELECT * FROM User WHERE `id`=?;", sqlMetaData.getSelectByIdSql());
    }

    @Test
    @DisplayName("должен корректно создавать INSERT запрос")
    void shouldGenerateCorrectInsert() {
        assertEquals("INSERT INTO User (name,age) VALUES (?,?);", sqlMetaData.getInsertSql());
    }

    @Test
    @DisplayName("должен корректно создавать UPDATE запрос")
    void shouldGenerateCorrectUpdate() {
        assertEquals("UPDATE User SET `name`=?,`age`=? WHERE `id`=?;", sqlMetaData.getUpdateSql());
    }
}