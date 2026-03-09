package pl.psi.aaas.engine.r;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import java.util.Objects;

class REngineConfiguration {
    private final String address;
    private final int port;

    public REngineConfiguration(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        REngineConfiguration that = (REngineConfiguration) o;
        return port == that.port && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public String toString() {
        return "REngineConfiguration(address=" + address + ", port=" + port + ")";
    }
}

public interface RConnectionProvider {
    REngineConfiguration getConfiguration();

    default RConnection getConnection() throws RserveException {
        return new RConnection(getConfiguration().getAddress(), getConfiguration().getPort());
    }
}
