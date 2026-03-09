package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.Rserve.RConnection;
import pl.psi.aaas.DataFrame;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.Primitive;
import pl.psi.aaas.Vector;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.time.ZonedDateTime;

public class RTransceiverFactory {
    public static RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf > get(Parameter<?> param, RConnection conn) throws CalculationException {
        if (param instanceof Primitive) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) primitiveTransceiver(((Primitive<?>) param).getClazz(), conn);
        } else if (param instanceof Vector) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) vectorTransceiver((Vector<?>) param, conn);
        } else if (param instanceof DataFrame) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) new DataFrameTransceiver(conn);
        }
        throw new CalculationException("Unsupported parameter type: " + param.getClass());
    }

    private static RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf > primitiveTransceiver(Class<?> clazz, RConnection conn) throws CalculationException {
        if (clazz == java.lang.String.class || clazz == String.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RPrimitiveTransceiver.string(conn);
        } else if (clazz == java.lang.Long.class || clazz == Long.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RPrimitiveTransceiver.longTransceiver(conn);
        } else if (clazz == java.lang.Double.class || clazz == Double.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RPrimitiveTransceiver.doubleTransceiver(conn);
        } else if (clazz == java.lang.Boolean.class || clazz == Boolean.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RPrimitiveTransceiver.booleanTransceiver(conn);
        } else if (clazz == ZonedDateTime.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) new DateTimeTransceiver(conn);
        } else {
            throw new CalculationException("Not implemented parameter type " + clazz);
        }
    }

    private static RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf > vectorTransceiver(Vector<?> param, RConnection conn) throws CalculationException {
        Class<?> elemClazz = param.getElemClazz();
        if (elemClazz == java.lang.String.class || elemClazz == String.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RArrayTransceiver.string(conn);
        } else if (elemClazz == java.lang.Long.class || elemClazz == Long.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RArrayTransceiver.longTransceiver(conn);
        } else if (elemClazz == java.lang.Double.class || elemClazz == Double.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RArrayTransceiver.doubleTransceiver(conn);
        } else if (elemClazz == java.lang.Boolean.class || elemClazz == Boolean.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) RArrayTransceiver.booleanTransceiver(conn);
        } else if (elemClazz == ZonedDateTime.class) {
            return (RValuesTransceiver<Parameter<?>, ?, CalculationDefinitionIf >) (Object) new ArrayDateTimeTransceiver(conn);
        } else {
            throw new CalculationException("Not implemented array parameter type " + elemClazz);
        }
    }
}
