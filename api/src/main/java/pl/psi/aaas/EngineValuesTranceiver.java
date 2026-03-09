package pl.psi.aaas;

import pl.psi.aaas.usecase.CalculationDefinitionIf;

/**
 * Implementations translate In/Out types ofPrimitive ValuesRepository into Engine's native representation.
 *
 * @param <V>
 *     read and save values type parameter
 * @param <R>
 *     read and save values type parameter
 * @param <D>
 *     calculation definition type
 * @param <S>
 *     session type
 */
public interface EngineValuesTranceiver< V, R, D extends CalculationDefinitionIf, S >
    extends EngineValuesSender< V, D >, EngineValuesReceiver< R, D >
{
    S getSession();
}
