package ru.otus.jdbc.mapper.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> type;
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
        var constructor = (Constructor<T>) Arrays.stream(type.getDeclaredConstructors())
                .filter(c -> isApplicableConstructor(c, allFields))
                .findFirst()
                .orElseThrow(() -> new MetadataException(
                        "Cannot find constructor " + type.getName() + "(" + fieldsToString(allFields) + ")"));

        constructor.setAccessible(true);
        allFields.forEach(field -> field.setAccessible(true));

        return new EntityClassMetaDataImpl<>(type, type.getSimpleName(), constructor, idField, allFields);
    }

    private static boolean isApplicableField(Field field) {
        var modifier = field.getModifiers();
        return !Modifier.isStatic(modifier);
    }

    private static boolean isApplicableConstructor(Constructor<?> constructor, List<Field> required) {
        if (constructor.getParameterCount() != required.size()) {
            return false;
        }
        var types = constructor.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (!required.get(i).getType().isAssignableFrom(types[i])) {
                return false;
            }
        }
        return true;
    }

    private static String fieldsToString(List<Field> fields) {
        return fields.stream().map(Field::getType).map(Class::getSimpleName).collect(Collectors.joining(", "));
    }
}
