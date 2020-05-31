package ru.otus.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сериализатор")
public class OtusonTest {
    private static Otuson otuson;
    private static Gson gson;

    @BeforeAll
    static void setUp() {
        otuson = Otuson.create();
        gson = new GsonBuilder().serializeNulls().create();
    }

    @Test
    @DisplayName("должен сериализовать примитивные типы и строки")
    void shouldSerializePrimitives() {
        assertEquals("\"String\"", otuson.toJson("String"));
        assertEquals("100", otuson.toJson(100));
        assertEquals("100", otuson.toJson((byte) 100));
        assertEquals("100", otuson.toJson((short) 100));
        assertEquals("100.1", otuson.toJson(100.1));
        assertEquals("10000000000", otuson.toJson(10000000000L));
        assertEquals("true", otuson.toJson(true));
        assertEquals("false", otuson.toJson(false));
        assertEquals("null", otuson.toJson(null));
    }

    @Test
    @DisplayName("должен сериализовать коллекции и массивы")
    void shouldSerializeCollectionsAndArrays() {
        var list = Arrays.asList(100, 500, 100500);
        var expecting = "[100,500,100500]";
        assertEquals(expecting, otuson.toJson(list));
        assertEquals(expecting, otuson.toJson(list.toArray(Integer[]::new)));
    }

    @Test
    @DisplayName("должен сериализовать сложные объекты")
    void shouldSerializeComplexObjects() {
        var object = new SomeObject();
        var json = otuson.toJson(object);
        var gsonObject = gson.toJsonTree(object);
        var parsedGsonObject = gson.fromJson(json, JsonObject.class);
        var objectFromJson = gson.fromJson(json, SomeObject.class);
        assertEquals(gsonObject, parsedGsonObject);
        assertEquals(object, objectFromJson);
    }
}
