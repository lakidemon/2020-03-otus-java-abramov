package ru.otus.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.jdbc.mapper.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private long id;
    private String name;
    private int age;
}
