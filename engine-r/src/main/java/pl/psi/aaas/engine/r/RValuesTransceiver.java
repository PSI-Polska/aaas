package pl.psi.aaas.engine.r;

import org.rosuda.REngine.Rserve.RConnection;
import pl.psi.aaas.EngineValuesTranceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;

/**
 *
 */
public interface RValuesTransceiver<V, R, D extends CalculationDefinitionIf > extends EngineValuesTranceiver<V, R, D, RConnection> {
}
