package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;

import java.util.Arrays;
import java.util.function.Function;

class RArrayTransceiver<V extends Parameter<Object[]>, R> extends RNativeTransceiver<V, R> {

    public RArrayTransceiver(RConnection session, Function<V, REXP> outTransformer, Function<REXP, R> inTransformer) {
        super(session, outTransformer, inTransformer);
    }

    public static RValuesTransceiver<Parameter<String[]>, String[], CalculationDefinitionIf > string(RConnection session) {
        return new RNativeTransceiver<>(
                session,
                v -> new REXPString(v.getValue()),
                r -> {
                    try {
                        return r.isString() ? r.asStrings() : new String[0];
                    } catch (RserveException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Long[]>, Long[], CalculationDefinitionIf > longTransceiver(RConnection session) {
        return new RNativeTransceiver<>(
                session,
                v -> {
                    double[] doubles = Arrays.stream(v.getValue())
                            .mapToDouble(l -> l != null ? l.doubleValue() : REXPDouble.NA)
                            .toArray();
                    return new REXPDouble(doubles);
                },
                r -> {
                    try {
                        if (r.isNumeric()) {
                            double[] doubles = r.asDoubles();
                            Long[] longs = new Long[doubles.length];
                            for (int i = 0; i < doubles.length; i++) {
                                longs[i] = (long) doubles[i];
                            }
                            return longs;
                        }
                        return new Long[0];
                    } catch (RserveException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Double[]>, Double[], CalculationDefinitionIf > doubleTransceiver(RConnection session) {
        return new RNativeTransceiver<>(
                session,
                v -> {
                    double[] doubles = Arrays.stream(v.getValue())
                            .mapToDouble(d -> d != null ? d : REXPDouble.NA)
                            .toArray();
                    return new REXPDouble(doubles);
                },
                r -> {
                    try {
                        if (r.isNumeric()) {
                            double[] doubles = r.asDoubles();
                            Double[] result = new Double[doubles.length];
                            for (int i = 0; i < doubles.length; i++) {
                                result[i] = doubles[i];
                            }
                            return result;
                        }
                        return new Double[0];
                    } catch (RserveException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static RValuesTransceiver<Parameter<Boolean[]>, Boolean[], CalculationDefinitionIf > booleanTransceiver(RConnection session) {
        return new RNativeTransceiver<>(
                session,
                v -> {
                    byte[] bytes = new byte[v.getValue().length];
                    for (int i = 0; i < v.getValue().length; i++) {
                        Boolean b = v.getValue()[i];
                        if (b == null) bytes[i] = REXPLogical.NA;
                        else if (b) bytes[i] = REXPLogical.TRUE;
                        else bytes[i] = REXPLogical.FALSE;
                    }
                    return new REXPLogical(bytes);
                },
                r -> {
                    try {
                        if (r.isLogical()) {
                            byte[] bytes = r.asBytes();
                            Boolean[] result = new Boolean[bytes.length];
                            for (int i = 0; i < bytes.length; i++) {
                                if (bytes[i] == REXPLogical.TRUE) result[i] = true;
                                else if (bytes[i] == REXPLogical.FALSE) result[i] = false;
                                else result[i] = null;
                            }
                            return result;
                        }
                        return new Boolean[0];
                    } catch (RserveException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}

