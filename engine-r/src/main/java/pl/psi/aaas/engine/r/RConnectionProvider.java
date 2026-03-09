package pl.psi.aaas.engine.r;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Interface that provides a means to obtain a connection to an R server. Implementing classes are responsible
 * for supplying the configuration details required to establish the connection.
 */
public interface RConnectionProvider
{
    REngineConfiguration getConfiguration();

    default RConnection getConnection() throws RserveException
    {
        return new RConnection( getConfiguration().getAddress(), getConfiguration().getPort() );
    }
}
