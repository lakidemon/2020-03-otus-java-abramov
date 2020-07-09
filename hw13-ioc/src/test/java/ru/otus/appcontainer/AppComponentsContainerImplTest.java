package ru.otus.appcontainer;

import org.junit.jupiter.api.Test;
import ru.otus.config.AppConfig;
import ru.otus.config.IOConfig;

import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AppComponentsContainerImplTest {

    @Test
    void shouldFindConfigClassesByPackage() {
        assertEquals(2, AppComponentsContainerImpl.findConfigurations("ru.otus.config").length);
    }

    @Test
    void shouldFindAnnotatedMethods() {
        assertEquals(4, AppComponentsContainerImpl.discoverComponentProviders(AppConfig.class).size());
        assertEquals(2, AppComponentsContainerImpl.discoverComponentProviders(IOConfig.class).size());
    }

    @Test
    void shouldFindComponentsByName() {
        var container = assertDoesNotThrow(() -> new AppComponentsContainerImpl(AppConfig.class, IOConfig.class));
        assertNotNull(container.getAppComponent("equationPreparer"));
        assertNotNull(container.getAppComponent("playerService"));
        assertNotNull(container.getAppComponent("gameProcessor"));
        assertNotNull(container.getAppComponent("ioService"));
    }

    @Test
    void shouldFindComponentsByClass() {
        var container = assertDoesNotThrow(() -> new AppComponentsContainerImpl("ru.otus.config"));
        assertNotNull(container.getAppComponent(PrintStream.class));
        assertNotNull(container.getAppComponent(InputStream.class));
    }
}