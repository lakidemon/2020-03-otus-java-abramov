package ru.otus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.jdbc.mapper.Id;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Account {
    @Id
    private Long no;
    private String type;
    private BigDecimal rest;

    public Account(String type, BigDecimal rest) {
        this(0L, type, rest);
    }

    public Account(Long no, String type, BigDecimal rest) {
        this.no = no;
        this.type = type;
        this.rest = rest;
    }

}
