package ru.otus.jdbc.mapper;

public class MapperException extends RuntimeException {

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        super(message);
    }
}
