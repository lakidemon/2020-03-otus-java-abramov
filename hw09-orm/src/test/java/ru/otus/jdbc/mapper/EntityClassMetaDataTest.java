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
        assertTrue(EntityClassMetaDataImpl.isInteger(long.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(int.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(byte.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(short.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(Long.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(Integer.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(Byte.class));
        assertTrue(EntityClassMetaDataImpl.isInteger(Short.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(Double.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(Float.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(double.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(float.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(BigDecimal.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(BigInteger.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(String.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(System.class));
        assertFalse(EntityClassMetaDataImpl.isInteger(AtomicInteger.class));
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
    @DisplayName("должен находить пустой конструктор")
    void shouldFindCorrectConstructor() {
        assertEquals(0, userMetadata().getConstructor().getParameterCount());
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
        assertEquals(0, classMetaData.getConstructor().getParameterCount());
        assertEquals("no", classMetaData.getIdField().getName());
    }

    EntityClassMetaData<User> userMetadata() {
        return EntityClassMetaDataImpl.create(User.class);
    }
}