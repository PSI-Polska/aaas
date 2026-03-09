package pl.psi.aaas;

import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
interface EngineValuesSender< V, D extends CalculationDefinitionIf >
{

    void send( String name, V value, D definition ) throws CalculationException;
}
