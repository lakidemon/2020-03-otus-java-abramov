package ru.otus.model;

import lombok.Getter;
import ru.otus.jdbc.mapper.Id;

@Getter
public class Account {
    @Id
    private long no;
    private String type;
    private int rest;

    public Account(String type, int rest) {
        this(0L, type, rest);
    }

    public Account(long no, String type, int rest) {
        this.no = no;
        this.type = type;
        this.rest = rest;
    }

}
