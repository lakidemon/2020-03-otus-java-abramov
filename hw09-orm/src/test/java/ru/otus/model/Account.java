package ru.otus.model;

import lombok.Data;
import ru.otus.jdbc.mapper.Id;

import java.math.BigDecimal;

@Data
public class Account {
    @Id
    private final long no;
    private String type;
    private BigDecimal rest;

    public Account(String type, BigDecimal rest) {
        this(0L, type, rest);
    }

    public Account(long no, String type, BigDecimal rest) {
        this.no = no;
        this.type = type;
        this.rest = rest;
    }

}
