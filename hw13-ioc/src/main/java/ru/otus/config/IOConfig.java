package ru.otus.config;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.io.InputStream;
import java.io.PrintStream;

@AppComponentsContainerConfig(order = 1)
public class IOConfig {

    @AppComponent(order = 0, name = "output")
    public PrintStream output() {
        return System.out;
    }

    @AppComponent(order = 0, name = "input")
    public InputStream input() {
        return System.in;
    }
}
