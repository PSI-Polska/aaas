package pl.psi.aaas;

import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
interface EngineValuesReceiver< R, D extends CalculationDefinitionIf >
{
    R receive( String name, Object result, D definition ) throws CalculationException;
}
