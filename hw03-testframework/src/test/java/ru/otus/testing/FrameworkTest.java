package ru.otus.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестовый фреймворк")
public class FrameworkTest {
    private TestsContainer testsContainer;

    @BeforeEach
    void createContainer() {
        testsContainer = new TestsContainer(ExampleTest.class);
        testsContainer.collectTests();
    }

    @DisplayName("должен обнаружить все тесты")
    @Test
    void shouldHaveAllTests() {
        assertEquals(5, testsContainer.getTests().size());
    }

    @DisplayName("должен обнаружить все @Before и @After методы")
    @Test
    void shouldHaveBeforeAndAfter() {
        var test = testsContainer.getTests().get(0);
        assertFalse(test.getBefore().isEmpty(), "@Before not found");
        assertFalse(test.getAfter().isEmpty(), "@After not found");
    }

    @DisplayName("должен упасть если класс без тестов")
    @Test
    void shouldFailIfClassHasNoTests() {
        var container = new TestsContainer(TestResult.class);
        container.collectTests();
        assertThrows(IllegalStateException.class, () -> container.startTests(null));
    }
}
