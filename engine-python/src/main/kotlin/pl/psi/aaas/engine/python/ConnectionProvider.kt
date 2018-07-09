package pl.psi.aaas.engine.python

interface ConnectionProvider<T> {
    fun getConnection(): T;
}