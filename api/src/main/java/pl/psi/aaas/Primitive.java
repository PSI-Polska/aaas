package pl.psi.aaas;

import java.util.Objects;

/**
 * Primitive value that can be sent and received to the {@link pl.psi.aaas.Engine}.
 *
 * Currently supported types are:
 * * String
 * * Long
 * * Double
 * * Boolean
 * * ZonedDateTime
 */
public class Primitive<T> extends Parameter<T> {

    Primitive(T value, Class<T> clazz) {
        super(value, clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Primitive<?> primitive = (Primitive<?>) o;
        return Objects.equals(value, primitive.value) &&
                Objects.equals(clazz, primitive.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, clazz);
    }

    @Override
    public String toString() {
        return "Primitive(value=" + value + ", clazz=" + clazz + ")";
    }
}
