package pl.psi.aaas;

import pl.psi.aaas.engine.r.RConnectionProvider;
import pl.psi.aaas.engine.r.REngineConfiguration;

import javax.ejb.Stateless;

/**
 * This stateless class is an implementation of the {@link RConnectionProvider} interface that provides
 * configuration for establishing a connection to an R server. It overrides the {@code getConfiguration}
 * method to return an {@link REngineConfiguration} object containing the connection details (address and
 * port) for the R server.
 */
@Stateless
public class LocalRConnectionProvider implements RConnectionProvider
{
    @Override
    public REngineConfiguration getConfiguration()
    {
        return new REngineConfiguration( "engine", 6311 );
    }
}
