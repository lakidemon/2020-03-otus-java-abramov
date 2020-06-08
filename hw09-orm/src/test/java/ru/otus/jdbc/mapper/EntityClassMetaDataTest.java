package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.core.model.User;
import ru.otus.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.metadata.MetadataException;
import ru.otus.model.Account;
import ru.otus.model.IncorrectModels;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EntityClassMetaDataTest {

    @Test
    @DisplayName("должен успешно создаваться")
    void shouldCreateUserData() {
        assertDoesNotThrow(this::userMetadata);
    }

    @Test
    @DisplayName("не должен создаваться для неподходящих классов")
    void shouldFailIfIncorrectModelProvided() {
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutAnnotation.class));
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutConstructor.class));
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithWrongIdTypeAnnotation.class));
        assertThrows(MetadataException.class,
                () -> EntityClassMetaDataImpl.create(IncorrectModels.WithoutRequiredConstructor.class));
    }

    @Test
    @DisplayName("должен допускать только определённые типы поля @Id")
    void shouldCheckIdTypeCorrectly() {
        assertTrue(EntityClassMetaDataImpl.isNumeric(long.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(int.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(byte.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(short.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(Long.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(Integer.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(Byte.class));
        assertTrue(EntityClassMetaDataImpl.isNumeric(Short.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(Double.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(Float.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(double.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(float.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(BigDecimal.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(BigInteger.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(String.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(System.class));
        assertFalse(EntityClassMetaDataImpl.isNumeric(AtomicInteger.class));
    }

    @Test
    @DisplayName("должен правильно находить @Id поле")
    void shouldFindCorrectIdField() {
        assertEquals("id", userMetadata().getIdField().getName());
    }

    @Test
    @DisplayName("должен находить все нужные поля")
    void shouldFindAllFields() {
        assertEquals(3, userMetadata().getAllFields().size());
    }

    @Test
    @DisplayName("должен находить подходящий конструктор")
    void shouldFindCorrectConstructor() {
        assertEquals(3, userMetadata().getConstructor().getParameterCount());
    }

    @Test
    @DisplayName("должен задавать правильное имя таблицы")
    void shouldHaveCorrectName() {
        assertEquals("User", userMetadata().getName());
    }

    @Test
    @DisplayName("должен поддерживать Account из дз")
    void shouldHandleAccountClassCorrectly() {
        var classMetaData = EntityClassMetaDataImpl.create(Account.class);
        assertEquals(3, classMetaData.getConstructor().getParameterCount());
        assertEquals("no", classMetaData.getIdField().getName());
    }

    EntityClassMetaData<User> userMetadata() {
        return EntityClassMetaDataImpl.create(User.class);
    }
}