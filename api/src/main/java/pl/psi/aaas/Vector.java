package pl.psi.aaas;

import java.util.Arrays;
import java.util.Objects;

/**
 * Vector of primitive values.
 * Supports only types supported by {@link Primitive}.
 */
public class Vector<T> extends Parameter<T[]> {
    private final Class<?> elemClazz;

    public Vector(T[] value, Class<T[]> clazz, Class<?> elemClazz) {
        super(value, clazz);
        this.elemClazz = elemClazz;
    }

    public Class<?> getElemClazz() {
        return elemClazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector<?> vector = (Vector<?>) o;
        return Arrays.equals(value, vector.value) &&
                Objects.equals(clazz, vector.clazz) &&
                Objects.equals(elemClazz, vector.elemClazz);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clazz, elemClazz);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "Vector(value=" + Arrays.toString(value) + ", clazz=" + clazz + ", elemClazz=" + elemClazz + ")";
    }
}
