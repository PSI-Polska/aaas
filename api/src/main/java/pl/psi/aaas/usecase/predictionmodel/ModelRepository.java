package pl.psi.aaas.usecase.predictionmodel;

import pl.psi.aaas.usecase.CalculationException;

/**
 * TODO not used anywhere yet!
 */
public interface ModelRepository {
    void readModel(String path) throws ModelAccessException;

    void saveModel(String path) throws ModelAccessException;
}

class ModelAccessException extends CalculationException {
    public ModelAccessException(String message) {
        super(message);
    }

    public ModelAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
