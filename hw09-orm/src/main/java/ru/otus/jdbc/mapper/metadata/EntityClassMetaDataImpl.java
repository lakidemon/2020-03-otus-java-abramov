package ru.otus.jdbc.mapper.metadata;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final String name;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;

    @Override
    public List<Field> getFieldsWithoutId() {
        return allFields.stream().filter(Predicate.isEqual(idField).negate()).collect(Collectors.toUnmodifiableList());
    }

    public static <T> EntityClassMetaData<T> create(Class<T> type) {
        var allFields = Arrays.stream(type.getDeclaredFields())
                .filter(EntityClassMetaDataImpl::isApplicableField)
                .collect(Collectors.toUnmodifiableList());
        var idField = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new MetadataException("No @Id field for type " + type.getName()));
        if (!isInteger(idField.getType())) {
            throw new MetadataException("Non-numeric @Id field type: " + idField.getType());
        }
        Constructor<T> constructor = null;
        try {
            constructor = type.getConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new MetadataException("Cannot find no-args constructor for " + type.getName(), e);
        }

        allFields.forEach(field -> field.setAccessible(true));

        return new EntityClassMetaDataImpl<>(type.getSimpleName(), constructor, idField, allFields);
    }

    public static boolean isInteger(@NonNull Class<?> type) {
        if (!type.isPrimitive()) {
            if (!Number.class.isAssignableFrom(type)) {
                return false;
            } else {
                try {
                    type = (Class<?>) type.getDeclaredField("TYPE").get(null);
                } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
                    return false;
                }
            }
        }
        return allowedIdTypes.contains(type);
    }

    private static Set<Class<? extends Number>> allowedIdTypes = Stream.of(int.class, long.class, short.class,
            byte.class).collect(Collectors.toSet());

    private static boolean isApplicableField(Field field) {
        var modifier = field.getModifiers();
        return !Modifier.isStatic(modifier);
    }

}
