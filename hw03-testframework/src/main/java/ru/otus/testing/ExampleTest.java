package ru.otus.testing;

import ru.otus.testing.annotations.After;
import ru.otus.testing.annotations.Before;
import ru.otus.testing.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {
    private List<Integer> sharedData = new ArrayList<>();

    @Before
    void fillSharedData() {
        IntStream.range(0, 5).forEach(sharedData::add);
    }

    @Test
    void successfulTest() {
        assertEquals(0, sharedData.get(0));
    }

    @Test
    void longSuccessfulTest() throws InterruptedException {
        Thread.sleep(2000l);
    }

    @Test
    void failingTest() {
        assertEquals(1, sharedData.get(0));
    }

    @Test
    void testThatThrowsException() {
        sharedData.get(5);
    }

    @Test
    void testThatThrowsException2() throws Throwable {
        throw new Throwable("Test");
    }

    @After
    void clearSharedData() {
        sharedData.clear();
    }
}
