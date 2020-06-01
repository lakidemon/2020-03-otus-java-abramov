package ru.otus.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    @DisplayName("должен сериализовать примитивные типы, строки, null и Object")
    void shouldSerializePrimitives() {
        assertEquals("\"String\"", otuson.toJson("String"));
        assertEquals("\"a\"", otuson.toJson('a'));
        assertEquals("100", otuson.toJson(100));
        assertEquals("100", otuson.toJson((byte) 100));
        assertEquals("100", otuson.toJson((short) 100));
        assertEquals("100.1", otuson.toJson(100.1));
        assertEquals("10000000000", otuson.toJson(10000000000L));
        assertEquals("true", otuson.toJson(true));
        assertEquals("false", otuson.toJson(false));
        assertEquals("null", otuson.toJson(null));
        assertEquals("{}", otuson.toJson(new Object()));
    }

    @Test
    @DisplayName("должен производить json аналогично gson")
    void shouldProduceSameJsonAsGson() {
        assertEquals(gson.toJson(null), otuson.toJson(null));
        assertEquals(gson.toJson((byte) 1), otuson.toJson((byte) 1));
        assertEquals(gson.toJson((short) 1f), otuson.toJson((short) 1f));
        assertEquals(gson.toJson(1), otuson.toJson(1));
        assertEquals(gson.toJson(1L), otuson.toJson(1L));
        assertEquals(gson.toJson(1f), otuson.toJson(1f));
        assertEquals(gson.toJson(1d), otuson.toJson(1d));
        assertEquals(gson.toJson("aaa"), otuson.toJson("aaa"));
        assertEquals(gson.toJson('a'), otuson.toJson('a'));
        assertEquals(gson.toJson(new int[] { 1, 2, 3 }), otuson.toJson(new int[] { 1, 2, 3 }));
        assertEquals(gson.toJson(List.of(1, 2, 3)), otuson.toJson(List.of(1, 2, 3)));
        assertEquals(gson.toJson(Collections.singletonList(1)), otuson.toJson(Collections.singletonList(1)));
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
