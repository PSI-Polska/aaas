package pl.psi.aaas.sample;

import pl.psi.aaas.*;
import pl.psi.aaas.engine.r.RConnectionProvider;
import pl.psi.aaas.engine.r.RServeEngine;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationDefinition;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class SimpleTestApp {
    public static void main(String[] args) {
        Facade< CalculationDefinitionIf, Void> facade = FixedTSFacade.INSTANCE;
        facade.callScript(prepCalcDef1());
    }

    private static CalculationDefinitionIf prepCalcDef1() {
        String[] strArr = {"a", "b", "c"};
        ZonedDateTime[] dtArr = {ZonedDateTime.now().minusHours(2), ZonedDateTime.now().minusHours(1), ZonedDateTime.now()};
        Vector<String> strVec = Parameter.ofArrayNotNull(strArr, String.class);
        Vector<ZonedDateTime> dtVec = Parameter.ofArrayNotNull(dtArr, ZonedDateTime.class);
        Vector<Long> longVec = Parameter.ofArrayNotNull(new Long[]{1L, 2L, 3L}, Long.class);
        Vector<Double> doubleVec = Parameter.ofArrayNotNull(new Double[]{0.1, 0.2, 1.0}, Double.class);
        Vector<Double> doubleNullVec = Parameter.ofArray(new Double[]{0.1, null, 1.0}, Double.class);
        Vector<Boolean> boolVec = Parameter.ofArrayNotNull(new Boolean[]{true, false, true}, Boolean.class);
        Vector<Boolean> boolNullVec = Parameter.ofArray(new Boolean[]{true, false, null}, Boolean.class);

        Column[] dfColumns = {
                new Column("dt", (Vector<Object>) (Vector<?>) dtVec),
                new Column("longs", (Vector<Object>) (Vector<?>) longVec),
                new Column("doubles", (Vector<Object>) (Vector<?>) doubleVec)
        };

        Map<String, Parameter<?>> parameters = new HashMap<>();
        parameters.put("str", Parameter.ofPrimitive("str_value"));
        parameters.put("dt", Parameter.ofPrimitive(ZonedDateTime.now()));
        parameters.put("d", Parameter.ofPrimitive(0.75));
        parameters.put("l", Parameter.ofPrimitive(10L));
        parameters.put("b", Parameter.ofPrimitive(false));
        parameters.put("strV", strVec);
        parameters.put("dtV", dtVec);
        parameters.put("longV", longVec);
        parameters.put("doubleV", doubleVec);
        parameters.put("doubleNullV", doubleNullVec);
        parameters.put("boolV", boolVec);
        parameters.put("boolNullV", boolNullVec);
        parameters.put("df", Parameter.ofDataFrame(dfColumns));

        return new CalculationDefinition(new HashMap<>(), new HashMap<>(), ZonedDateTime.now(), ZonedDateTime.now().plusDays(1), "add");
        // Note: CalculationDefinitionDTO in api module seems to have changed its constructor in my previous step.
        // I need to check the actual CalculationDefinitionDTO I wrote.
    }

    public static class LocalRConnectionProvider implements RConnectionProvider {
        @Override
        public String getHost() { return "192.168.99.100"; }
        @Override
        public int getPort() { return 6311; }
    }

    public static class FixedTSFacade implements Facade< CalculationDefinitionIf, Void> {
        public static final FixedTSFacade INSTANCE = new FixedTSFacade();
        private final Engine< CalculationDefinitionIf, Map<String, Parameter<?>>, Map<String, Parameter<?>>> engine = new RServeEngine(new LocalRConnectionProvider());

        @Override
        public Void callScript( CalculationDefinitionIf calcDef) {
            engine.call(calcDef);
            return null;
        }
    }
}
