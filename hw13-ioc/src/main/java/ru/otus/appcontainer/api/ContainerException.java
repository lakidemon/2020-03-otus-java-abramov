package ru.otus.appcontainer.api;

public class ContainerException extends RuntimeException {
    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(String message) {
        super(message);
    }
}
