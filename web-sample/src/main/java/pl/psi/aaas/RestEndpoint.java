package pl.psi.aaas;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import pl.psi.aaas.usecase.CalculationDefinition;
import pl.psi.aaas.usecase.CalculationDefinitionIf;

@Path("/call")
public class RestEndpoint {

    private final Facade< CalculationDefinitionIf, Void> facade;

    @Inject
    public RestEndpoint(Facade< CalculationDefinitionIf, Void> facade) {
        this.facade = facade;
    }

    @GET
    public void callDef() {
        facade.callScript(getDefinition());
    }

    public CalculationDefinitionIf getDefinition() {
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

        return new CalculationDefinition( "add" );
    }
}
