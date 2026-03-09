package pl.psi.aaas;

import pl.psi.aaas.engine.r.RConnectionProvider;
import javax.ejb.Stateless;

@Stateless
public class LocalRConnectionProvider implements RConnectionProvider {
    @Override
    public String getHost() {
        return "engine";
    }

    @Override
    public int getPort() {
        return 6311;
    }
}
