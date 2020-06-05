package ru.otus.json;

import javax.json.JsonValue;

public interface Converter<T> {

    JsonValue toJson(T object, OtusonContext context);

}
