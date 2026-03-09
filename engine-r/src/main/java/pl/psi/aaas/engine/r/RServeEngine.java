package pl.psi.aaas.engine.r;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psi.aaas.Engine;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.transceiver.RTransceiverFactory;
import pl.psi.aaas.usecase.CalculationDefinition;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class RServeEngine<D extends CalculationDefinition, V, R> implements Engine<D, V, R> {
    private static final Logger log = LoggerFactory.getLogger(RServeEngine.class);
    private static final String DEFAULT_BASE_USER_SCRIPT_PATH = "/var/userScripts/";

    private final RConnectionProvider connectionProvider;
    private final String baseUserScriptPath;

    public RServeEngine(RConnectionProvider connectionProvider) {
        this(connectionProvider, DEFAULT_BASE_USER_SCRIPT_PATH);
    }

    public RServeEngine(RConnectionProvider connectionProvider, String baseUserScriptPath) {
        this.connectionProvider = connectionProvider;
        this.baseUserScriptPath = baseUserScriptPath;
    }

    @Override
    public R call(D calcDef) throws CalculationException {
        try {
            RConnection conn = connectionProvider.getConnection();
            try {
                log.debug("Evaluating {}", calcDef);

                sourceScript(conn, calcDef, baseUserScriptPath);
                conn.voidEval("env <- environment()");

                for (Map.Entry<String, Parameter<?>> entry : calcDef.timeSeriesIdsIn().entrySet()) {
                    RValuesTransceiver<Parameter<?>, ?, D> t = (RValuesTransceiver<Parameter<?>, ?, D>) RValuesTransceiver.get(entry.getValue(), conn);
                    t.send(entry.getKey(), entry.getValue(), calcDef);
                }

                debugR(calcDef.getInParameters(), conn);

                log.debug("Calling script");
                conn.eval("run(env)");

                Map<String, Parameter<?>> retMap = new HashMap<>();
                for (Map.Entry<String, Parameter<?>> entry : calcDef.getOutParameters().entrySet()) {
                    RValuesTransceiver<Parameter<?>, ?, D> t = (RValuesTransceiver<Parameter<?>, ?, D>) RValuesTransceiverFactory.get(entry.getValue(), conn);
                    Object receivedValue = t.receive(entry.getKey(), null, calcDef);
                    retMap.put(entry.getKey(), (Parameter<?>) receivedValue);
                }

                log.debug(retMap.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("\n")));

                calcDef.setOutParameters(retMap);
                return (R) retMap;
            } finally {
                if (conn != null && conn.isConnected()) {
                    conn.close();
                }
            }
        } catch (RserveException ex) {
            log.error("Error during R calculation", ex);
            throw new CalculationException(ex.getMessage() != null ? ex.getMessage() : "There was an error during calculation.", ex);
        } catch (Exception ex) {
            log.error("Unexpected error during R calculation", ex);
            throw new CalculationException("Unexpected error during calculation: " + ex.getMessage(), ex);
        }
    }

    private void debugR(Map<String, Parameter<?>> parameters, RConnection conn) throws RserveException {
        for (String key : parameters.keySet()) {
            conn.voidEval("print(\"## " + key + "\")");
            conn.voidEval("str(" + key + ")");
        }
    }

    private static void sourceScript(RConnection conn, CalculationDefinitionIf calcDef, String scriptPath) throws RserveException {
        String path = scriptPath + calcDef.getCalculationScript() + ".R";
        log.debug("Sourcing: {}", path);
        conn.voidEval("writeLines(\"##\\nStarted execution ofPrimitive: " + path + "\\n##\")");
        conn.voidEval("source(\"" + path + "\")");
    }
}
