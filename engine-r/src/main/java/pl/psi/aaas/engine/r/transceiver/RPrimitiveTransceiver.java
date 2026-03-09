package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;

import java.util.function.Function;

class RPrimitiveTransceiver<V extends Parameter<?>, R> extends RNativeTransceiver<V, R> {

    public RPrimitiveTransceiver(RConnection session, Function<V, REXP> outTransformer, Function<REXP, R> inTransformer) {
        super(session, outTransformer, inTransformer);
    }

    public static RValuesTransceiver<Parameter<String>, String, CalculationDefinitionIf > string(RConnection session) {
        return new RPrimitiveTransceiver<>(
                session,
                v -> {
                    try {
                        return new REXPString(v.getValue());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                r -> {
                    try {
                        return r.isString() ? r.asString() : null;
                    } catch ( REXPMismatchException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Long>, Long, CalculationDefinitionIf > longTransceiver(RConnection session) {
        return new RPrimitiveTransceiver<>(
                session,
                v -> new REXPDouble(v.getValue().doubleValue()),
                r -> {
                    try {
                        return r.isNumeric() ? (long) r.asDouble() : null;
                    } catch (REXPMismatchException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Double>, Double, CalculationDefinitionIf > doubleTransceiver(RConnection session) {
        return new RPrimitiveTransceiver<>(
                session,
                v -> new REXPDouble(v.getValue()),
                r -> {
                    try {
                        return r.isNumeric() ? r.asDouble() : null;
                    } catch (REXPMismatchException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Boolean>, Boolean, CalculationDefinitionIf > booleanTransceiver(RConnection session) {
        return new RPrimitiveTransceiver<>(
                session,
                v -> new REXPLogical(v.getValue()),
                r -> {
                    try {
                        if (r.isLogical()) {
                            int val = r.asInteger();
                            if (val == REXPLogical.TRUE) return true;
                            if (val == REXPLogical.FALSE) return false;
                            return null;
                        }
                        return null;
                    } catch (REXPMismatchException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}

