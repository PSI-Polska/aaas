package pl.psi.aaas;

import pl.psi.aaas.engine.r.RConnectionProvider;
import pl.psi.aaas.engine.r.RServeEngine;
import pl.psi.aaas.usecase.CalculationDefinitionIf;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Map;

@Startup
@Singleton
public class CdiFacade implements Facade< CalculationDefinitionIf, Void> {
    @Inject
    private RConnectionProvider connection;

    private Engine< CalculationDefinitionIf, Map<String, Parameter<?>>, Map<String, Parameter<?>>> engine;

    @PostConstruct
    private void init() {
        engine = new RServeEngine(connection);
    }

    @Override
    public Void callScript( CalculationDefinitionIf calcDef) {
        engine.call(calcDef);
        return null;
    }
}
