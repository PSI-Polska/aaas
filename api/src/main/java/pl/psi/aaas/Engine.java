package pl.psi.aaas;

import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

/**
 * The calculation engine implementations are used by use cases implementations (CalculationExecution).
 */
public interface Engine<T extends CalculationDefinitionIf, V, R> {
    /**
     * Call the calculation definition on the engine.
     *
     * @param calcDef CalculationDefinition passed to the engine
     *
     * @throws CalculationException if calculation fails
     */
    R call(T calcDef) throws CalculationException;
}

