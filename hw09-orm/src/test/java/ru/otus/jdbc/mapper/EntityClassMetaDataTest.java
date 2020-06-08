package ru.otus.jdbc.mapper;

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
    void shouldCreateUserData() {
        assertDoesNotThrow(this::userMetadata);
    }

    @Test
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
    void shouldFindCorrectIdField() {
        assertEquals("id", userMetadata().getIdField().getName());
    }

    @Test
    void shouldFindAllFields() {
        assertEquals(3, userMetadata().getAllFields().size());
    }

    @Test
    void shouldFindCorrectConstructor() {
        assertEquals(3, userMetadata().getConstructor().getParameterCount());
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("User", userMetadata().getName());
    }

    @Test
    void shouldHandleAccountClassCorrectly() {
        var classMetaData = EntityClassMetaDataImpl.create(Account.class);
        assertEquals(3, classMetaData.getConstructor().getParameterCount());
        assertEquals("no", classMetaData.getIdField().getName());
    }

    EntityClassMetaData<User> userMetadata() {
        return EntityClassMetaDataImpl.create(User.class);
    }
}