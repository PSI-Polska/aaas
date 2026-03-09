package pl.psi.aaas.engine;

import pl.psi.aaas.usecase.ScriptSynchronizer;

/**
 * This synchronizer does no synchronization at all.
 */
public class NoSynchronizationSynchronizer implements ScriptSynchronizer {
    @Override
    public boolean isUnderSynchronization() {
        return false;
    }

    @Override
    public void waitEnd() {
    }
}
