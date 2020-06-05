package ru.otus.json;

public class UnsupportedTypeException extends RuntimeException {

    public UnsupportedTypeException(Class<?> type) {
        super("No applicable adapter for " + type.getName());
    }
}
