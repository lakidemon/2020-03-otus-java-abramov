package ru.otus.atm.banknote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Ruble implements Banknote {
    _10(10),
    _50(50),
    _100(100),
    _200(200),
    _500(500),
    _1000(1000),
    _2000(2000),
    _5000(5000);
    private final int value;
}
