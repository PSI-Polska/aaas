package pl.psi.aaas;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents DataFrame - array of names, heterogeneous {@link Vector}s.
 * Only types supported by {@link Vector} can be used.
 */
public class DataFrame extends Parameter<Column[]> {
    private final Class<?>[] columnClasses;

    public DataFrame(Column[] value, Class<?>[] columnClasses) {
        this(value, (Class<Column[]>) value.getClass(), columnClasses);
    }

    public DataFrame(Column[] value, Class<Column[]> clazz, Class<?>[] columnClasses) {
        super(value, clazz);
        this.columnClasses = columnClasses;
    }

    public Class<?>[] getColumnClasses() {
        return columnClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFrame dataFrame = (DataFrame) o;
        return Arrays.equals(value, dataFrame.value) &&
                Objects.equals(clazz, dataFrame.clazz) &&
                Arrays.equals(columnClasses, dataFrame.columnClasses);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clazz);
        result = 31 * result + Arrays.hashCode(value);
        result = 31 * result + Arrays.hashCode(columnClasses);
        return result;
    }

    @Override
    public String toString() {
        return "DataFrame(value=" + Arrays.toString(value) + ", clazz=" + clazz + ", columnClasses=" + Arrays.toString(columnClasses) + ")";
    }
}
