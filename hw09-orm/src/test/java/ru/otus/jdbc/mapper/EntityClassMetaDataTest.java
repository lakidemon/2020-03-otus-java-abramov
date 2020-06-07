package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.core.model.User;
import ru.otus.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.metadata.MetadataException;
import ru.otus.model.Account;
import ru.otus.model.IncorrectModels;

import static org.junit.jupiter.api.Assertions.*;

class EntityClassMetaDataTest {
    static EntityClassMetaData<User> classMetaData;

    @BeforeAll
    static void setup() {
        classMetaData = EntityClassMetaDataImpl.create(User.class);
    }

    @Test
    void shouldFailIfIncorrectModelProvided() {
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutAnnotation.class));
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutConstructor.class));
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutRequiredConstructor.class));
    }

    @Test
    void shouldFindCorrectIdField() {
        assertEquals("id", classMetaData.getIdField().getName());
    }

    @Test
    void shouldFindAllFields() {
        assertEquals(3, classMetaData.getAllFields().size());
    }

    @Test
    void shouldFindCorrectConstructor() {
        assertEquals(3, classMetaData.getConstructor().getParameterCount());
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("User", classMetaData.getName());
    }

    @Test
    void shouldHandleAccountClassCorrectly() {
        var classMetaData = EntityClassMetaDataImpl.create(Account.class);
        assertEquals(3, classMetaData.getConstructor().getParameterCount());
        assertEquals("no", classMetaData.getIdField().getName());
    }
}