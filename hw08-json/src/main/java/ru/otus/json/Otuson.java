package ru.otus.json;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Otuson {
    private final LinkedList<Adapter> adapters = new LinkedList<>();
    private final JsonProvider provider = JsonProvider.provider();

    public String toJson(Object object) {
        return toJsonValue(object).toString();
    }

    public JsonValue toJsonValue(Object object) {
        if (object == null) {
            return JsonValue.NULL;
        }
        if (object instanceof JsonValue) {
            return (JsonValue) object;
        }

        var context = new OtusonContext(this, provider);
        var objectType = object.getClass();

        var adapter = matchAdapter(objectType);
        return adapter.adapt(object, context);
    }

    Adapter matchAdapter(Class type) {
        return adapters.stream()
                .filter(a -> a.isApplicable(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedTypeException(type)); // в принципе, это исключение невозможно спровоцировать
    }

    public <T> void registerTypeConverter(Class type, Converter<T> converter) {
        registerAdapter(new Adapter(converter) {
            @Override
            public boolean isApplicable(Class clazz) {
                return type.isAssignableFrom(clazz);
            }
        });
    }

    public void registerAdapter(Adapter adapter) {
        adapters.addFirst(adapter);
    }

    public static Otuson create() {
        var otuson = new Otuson();
        otuson.registerAdapter(new Adapter((object, context) -> context.getJson().createObjectBuilder().build()) {
            @Override
            public boolean isApplicable(Class clazz) {
                return clazz.equals(Object.class);
            }
        });
        otuson.registerAdapter(new Adapter(Converters.OBJECT_ADAPTER) {
            @Override
            public boolean isApplicable(Class clazz) {
                return !Object.class.equals(clazz);
            }
        });
        otuson.registerTypeConverter(Map.class, Converters.MAP_ADAPTER);
        otuson.registerTypeConverter(Collection.class, Converters.COLLECTION_ADAPTER);
        otuson.registerAdapter(new Adapter(Converters.ARRAY_ADAPTER) {
            @Override
            public boolean isApplicable(Class clazz) {
                return clazz.isArray();
            }
        });
        otuson.registerTypeConverter(Boolean.class, Converters.BOOLEAN_ADAPTER);
        otuson.registerTypeConverter(Number.class, Converters.NUMBER_ADAPTER);
        otuson.registerTypeConverter(String.class, Converters.STRING_ADAPTER);
        return otuson;
    }
}
