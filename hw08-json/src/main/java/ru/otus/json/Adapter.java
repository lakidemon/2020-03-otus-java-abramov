package ru.otus.json;

import lombok.RequiredArgsConstructor;

import javax.json.JsonValue;

@RequiredArgsConstructor
public abstract class Adapter {
    protected final Converter converter;

    public abstract boolean isApplicable(Class clazz);

    public JsonValue adapt(Object object, OtusonContext context) {
        return converter.toJson(object, context);
    }
}
