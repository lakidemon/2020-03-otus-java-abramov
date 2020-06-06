package ru.otus.core.model;

import lombok.Data;
import ru.otus.jdbc.mapper.Id;

@Data
public class User {
    @Id
    private final long id;
    private final String name;
    private final int age;
}
