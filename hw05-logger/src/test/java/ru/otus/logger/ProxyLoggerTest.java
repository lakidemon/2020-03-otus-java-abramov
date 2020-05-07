package ru.otus.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import ru.otus.logger.object.AdvancedFinancesHandler;
import ru.otus.logger.object.FinancesHandler;
import ru.otus.logger.object.LoggableHandler;
import ru.otus.logger.proxy.ConstructorParams;
import ru.otus.logger.proxy.HandlerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Прокси логгер")
public class ProxyLoggerTest {
    private ByteArrayOutputStream buffer;
    private PrintStream originalOut;
    private LoggableHandler handler;

    @BeforeEach
    void prepare() {
        handler = HandlerFactory.createLoggableHandler(FinancesHandler.class);
        buffer = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(buffer));
    }

    @Test
    @DisplayName("должен успешно создать прокси-объект")
    void shouldBeProxy() {
        assertTrue(Proxy.isProxyClass(handler.getClass()), "Not a proxy object: " + handler.getClass());
    }

    @Test
    @DisplayName("должен найти все логируемые методы")
    void shouldFindLoggableMethod() throws Exception {
        Object invocationHandler = Proxy.getInvocationHandler(handler);
        var methods = (Set<Method>) ReflectionUtils.tryToReadFieldValue((Class) invocationHandler.getClass(),
                "methodsToLog", invocationHandler).get();
        assertFalse(methods.isEmpty(), "Annotated method not found");
        assertTrue(methods.stream().allMatch(method -> method.getName().equals("loggableMethod")),
                "Found wrong method: " + methods);
    }

    @Test
    @DisplayName("должен отработать на логируемом методе")
    void shouldLogAnnotatedMethod() {
        handler.loggableMethod("Me", 100);

        var result = buffer.toString();
        assertEquals(String.join(System.lineSeparator(), "loggableMethod called with args [Me, 100]",
                "User: Me, income: 100", ""), result, result);
    }

    @Test
    @DisplayName("должен ничего не делать на не-логируемом методе")
    void shouldNotLogNotAnnotatedMethod() {
        handler.notLoggableMethod("Somebody", 200);

        var result = buffer.toString();
        assertEquals("User: Somebody, loss: 200" + System.lineSeparator(), result, result);
    }

    @Test
    @DisplayName("должен успешно создать прокси для класса с конструктором с аргументами")
    void shouldCreateProxyForObjectsWithNonDefaultConstructor() {
        assertDoesNotThrow(() -> HandlerFactory.createLoggableHandler(AdvancedFinancesHandler.class,
                new ConstructorParams(new Class[] { String.class, BigDecimal.class },
                        new Object[] { "StringValue", BigDecimal.TEN })));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
