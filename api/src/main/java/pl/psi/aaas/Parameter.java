package pl.psi.aaas;

import pl.psi.aaas.usecase.DataFrame;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parameter class represents all values communicated with the {@link pl.psi.aaas.Engine} (both ways).
 * Parameter is used by EngineValuesSender and EngineValuesReceiver.
 * Parameter has four implementations:
 * * {@link Primitive}
 * * {@link Vector}
 * * Matrix (not yet implemented)
 * * {@link DataFrame}
 *
 * Currently supported types are:
 * * String
 * * Long
 * * Double
 * * Boolean
 * * ZonedDateTime
 */
public abstract class Parameter<T> {
    protected T value;
    protected final Class<T> clazz;

    protected Parameter(T value, Class<T> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public static final List<Class<?>> supportedClasses = Arrays.asList(
            java.lang.String.class, String.class,
            java.lang.Long.class, Long.class,
            java.lang.Double.class, Double.class,
            java.lang.Boolean.class, Boolean.class,
            ZonedDateTime.class,
            Vector.class);

    public static <T> Primitive<T> ofPrimitive(T value) {
        Class<?> clazz = value.getClass();
        if (isSupported(clazz)) {
            return new Primitive<>(value, (Class<T>) clazz);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + clazz);
        }
    }

    public static <T> Vector<T> ofArray(T[] value, Class<T> elemClazz) {
        if (isSupported(elemClazz)) {
            return new Vector<>(value, (Class<T[]>) value.getClass(), elemClazz);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + elemClazz.getCanonicalName());
        }
    }

    public static <T> Vector<T> ofArray(T[] value) {
        if (value.length == 0) {
            throw new IllegalArgumentException("Cannot infer element type from empty array");
        }
        return ofArray(value, (Class<T>) value.getClass().getComponentType());
    }

    public static <T> Vector<T> ofArrayNotNull(T[] value, Class<T> elemClazz) {
        return ofArray(value, elemClazz);
    }

    public static DataFrame ofDataFrame(Column[] value) {
        List<Class<?>> columnClasses = Arrays.stream(value)
                .map(col -> col.getVector().getElemClazz())
                .collect(Collectors.toList());

        List<Class<?>> unsupported = columnClasses.stream()
                .filter(clazz -> !isSupported(clazz))
                .collect(Collectors.toList());

        if (!unsupported.isEmpty()) {
            String notSupportedClasses = unsupported.stream()
                    .map(Class::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Unsupported types: " + notSupportedClasses);
        }

        long distinctSizes = Arrays.stream(value)
                .map(col -> col.getVector().getValue().length)
                .distinct()
                .count();

        if (distinctSizes != 1 && value.length > 0) {
            throw new IllegalArgumentException("All columns must have the same size");
        }

        return new DataFrame(value, columnClasses.toArray(new Class[0]));
    }

    private static boolean isSupported(Class<?> clazz) {
        return supportedClasses.contains(clazz);
    }
}
