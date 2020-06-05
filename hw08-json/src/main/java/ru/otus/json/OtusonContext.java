package ru.otus.json;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.json.spi.JsonProvider;

@RequiredArgsConstructor
@Getter
public class OtusonContext {
    private final Otuson otuson;
    private final JsonProvider json;
}
