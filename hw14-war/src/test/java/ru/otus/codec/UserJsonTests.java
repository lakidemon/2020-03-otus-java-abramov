package ru.otus.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.otus.model.Address;
import ru.otus.model.Phone;
import ru.otus.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserJsonTests {

    @Test
    void deserialize() {
        var objectMapper = new ObjectMapper();
        var user = assertDoesNotThrow(() -> objectMapper.readValue(
                "{\"name\":\"Тестовый Аккаунт\",\"age\":\"12\",\"address\":\"ул. Пушкина\","
                        + "\"phones\":[\"+70000000000\",\"+70000000001\"]}", User.class));
        assertNotNull(user);
        assertEquals("Тестовый Аккаунт", user.getName());
        assertEquals(12, user.getAge());
        assertEquals(new Address("ул. Пушкина"), user.getAddress());
        assertIterableEquals(List.of(new Phone("+70000000000"), new Phone("+70000000001")), user.getPhones());
    }

    @Test
    void serialize() {
        var objectMapper = new ObjectMapper();
        var json = assertDoesNotThrow(() -> objectMapper.writeValueAsString(
                new User("Тестовый Аккаунт", 12, new Address("ул. Пушкина"),
                        List.of(new Phone("+70000000000"), new Phone("+70000000001")))));

        assertEquals("{\"name\":\"Тестовый Аккаунт\",\"age\":12,\"address\":\"ул. Пушкина\","
                + "\"phones\":[\"+70000000000\",\"+70000000001\"]}", json);
    }
}