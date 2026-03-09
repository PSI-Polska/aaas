package pl.psi.aaas.usecase;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Very crude implementation of Primitive DataFrame.
 * When moving to Java8 think about moving to external implementation like ch.netzwerg.paleo.DataFrame.
 */
public class DataFrame<T> {
    private final Map<String, Integer> columns;
    private final T[][] matrix;

    public DataFrame(Map<String, Integer> columns, T[][] matrix) {
        this.columns = columns;
        this.matrix = matrix;
    }

    public DataFrame(String[] columns, T[][] matrix) {
        this(arrayToMap(columns), matrix);
    }

    public T[] get(int row) {
        if (row < matrix.length) {
            return matrix[row];
        } else {
            return null;
        }
    }

    public T[] get(String colName) {
        if (columns.containsKey(colName)) {
            return get(columns.get(colName));
        } else {
            return null;
        }
    }

    public T[][] getAll() {
        return matrix;
    }

    public List<String> getColumns() {
        return columns.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public int size() {
        if (matrix.length > 0) {
            return matrix[0].length;
        } else {
            return 0;
        }
    }

    /**
     * Returns a view of Primitive the DataFrame with predicate applied to columns.
     */
    public DataFrame<T> getFiltered(Predicate<String> predicate) {
        Map<String, Integer> filtered = columns.entrySet().stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new DataFrame<>(filtered, matrix);
    }

    public Map<String, T[]> toMap() {
        return columns.keySet().stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> get(key)
                ));
    }

    private static Map<String, Integer> arrayToMap(String[] arr) {
        return IntStream.range(0, arr.length)
                .boxed()
                .collect(Collectors.toMap(i -> arr[i], i -> i));
    }
}
