package pl.psi.aaas;

import java.util.Objects;

/**
 * Column of DataFrame consists of:
 * @param symbol column name
 * @param vector rows of given column
 */
public class Column {
    private final String symbol;
    private final Vector<?> vector;

    public Column(String symbol, Vector<?> vector) {
        this.symbol = symbol;
        this.vector = vector;
    }

    public String getSymbol() {
        return symbol;
    }

    public Vector<?> getVector() {
        return vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(symbol, column.symbol) &&
                Objects.equals(vector, column.vector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, vector);
    }

    @Override
    public String toString() {
        return "Column(symbol=" + symbol + ", vector=" + vector + ")";
    }
}
