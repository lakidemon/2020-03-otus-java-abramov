package ru.otus.core.model;

import lombok.Data;
import lombok.NonNull;
import ru.otus.jdbc.mapper.Id;

@Data
public class User {
    @Id
    private final long id;
    private @NonNull String name;
    private final int age;
}
