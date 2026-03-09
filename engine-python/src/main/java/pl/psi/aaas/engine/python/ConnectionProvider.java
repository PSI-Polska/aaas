package pl.psi.aaas.engine.python;

public interface ConnectionProvider<T> {
    T getConnection();
}
