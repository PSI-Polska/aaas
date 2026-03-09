package pl.psi.aaas;

import pl.psi.aaas.usecase.CalculationDefinitionIf;

/**
 *
 */
public interface Facade<T extends CalculationDefinitionIf, R> {
    R callScript(T calcDef);
}
