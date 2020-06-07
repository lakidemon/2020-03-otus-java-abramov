package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.core.model.User;
import ru.otus.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.metadata.EntitySQLMetaDataImpl;

import static org.junit.jupiter.api.Assertions.*;

class EntitySQLMetaDataTest {
    private static EntitySQLMetaData sqlMetaData;

    @BeforeAll
    static void setup() {
        sqlMetaData = EntitySQLMetaDataImpl.createFromClass(EntityClassMetaDataImpl.create(User.class));
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