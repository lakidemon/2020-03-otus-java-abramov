package ru.otus.json;

import lombok.SneakyThrows;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Converters {

    public static final Converter<String> STRING_ADAPTER = (object, context) -> context.getJson().createValue(object);
    public static final Converter<Number> NUMBER_ADAPTER = (object, context) -> {
        var json = context.getJson();
        if (Integer.class.isInstance(object) || Byte.class.isInstance(object) || Short.class.isInstance(object)) {
            return json.createValue(object.intValue());
        } else if (Long.class.isInstance(object)) {
            return json.createValue(object.longValue());
        } else if (Double.class.isInstance(object)) {
            return json.createValue(object.doubleValue());
        } else if (Float.class.isInstance(object)) {
            return json.createValue(object.floatValue());
        } else if (BigDecimal.class.isInstance(object)) {
            return json.createValue((BigDecimal) object);
        } else if (BigInteger.class.isInstance(object)) {
            return json.createValue((BigInteger) object);
        }
        throw new IllegalArgumentException("Unexpected value " + object);
    };
    public static final Converter<Boolean> BOOLEAN_ADAPTER = (object, context) -> object ?
            JsonValue.TRUE :
            JsonValue.FALSE;
    public static final Converter<Collection> COLLECTION_ADAPTER = (object, context) -> {
        var builder = context.getJson().createArrayBuilder();
        object.stream().map(context.getOtuson()::toJsonValue).forEach(o -> builder.add((JsonValue) o));
        return builder.build();
    };
    public static final Converter ARRAY_ADAPTER = (object, context) -> {
        var builder = context.getJson().createArrayBuilder();
        for (int i = 0; i < Array.getLength(object); i++) {
            builder.add(context.getOtuson().toJsonValue(Array.get(object, i)));
        }
        return builder.build();
    };
    public static final Converter<Map> MAP_ADAPTER = Converters::objectFromMap;
    public static final Converter OBJECT_ADAPTER = Converters::adaptObject;

    @SneakyThrows
    private static JsonObject adaptObject(Object o, OtusonContext context) {
        var map = new LinkedHashMap<String, Object>();
        for (Field field : getAllFields(o.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()))
                continue;
            field.setAccessible(true);
            var fieldValue = field.get(o);
            map.put(field.getName(), fieldValue);
        }
        return objectFromMap(map, context);
    }

    private static JsonObject objectFromMap(Map map, OtusonContext context) {
        var builder = context.getJson().createObjectBuilder();
        map.forEach((name, value) -> builder.add(String.valueOf(name), context.getOtuson().toJsonValue(value)));
        return builder.build();
    }

    private static Collection<Field> getAllFields(Class clazz) {
        var list = new ArrayList<Field>();
        list.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (!clazz.getSuperclass().equals(Object.class)) {
            list.addAll(getAllFields(clazz.getSuperclass()));
        }
        return list;
    }
}
