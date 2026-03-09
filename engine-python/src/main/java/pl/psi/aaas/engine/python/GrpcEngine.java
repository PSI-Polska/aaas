package pl.psi.aaas.engine.python;

import pl.psi.aaas.Engine;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.Vector;
import pl.psi.aaas.communication.*;
import pl.psi.aaas.usecase.CalculationDefinition;
import pl.psi.aaas.usecase.CalculationException;
import pl.psi.aaas.usecase.Parameters;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class GrpcEngine implements Engine<CalculationDefinition, Parameters, Parameters> {

    private final ConnectionProvider<AAASGrpc.AAASBlockingStub> connectionProvider;

    public GrpcEngine(ConnectionProvider<AAASGrpc.AAASBlockingStub> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Parameters call(CalculationDefinition calcDef) {
        Map<String, pl.psi.aaas.communication.Parameter> mappedParams = calcDef.getInParameters().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toCommParams(entry.getValue())));

        pl.psi.aaas.communication.Parameters paramsToBeSent = pl.psi.aaas.communication.Parameters.newBuilder()
                .putAllValue(mappedParams)
                .build();

        AAASGrpc.AAASBlockingStub client = connectionProvider.getConnection();

        CallParameters callParams = CallParameters.newBuilder()
                .setParams(paramsToBeSent)
                .setScript(calcDef.getCalculationScript())
                .build();
        try {
            CallParametersResponse retParams = client.call(callParams);
            if (!retParams.getErrorMessage().isEmpty()) {
                throw new CalculationException(retParams.getErrorMessage());
            }
            Map<String, Parameter<?>> resultParams = retParams.getParams().getValueMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> fromCommParams(entry.getValue().getVector(), calcDef.getOutParameters().get(entry.getKey()))
                    ));
            return new Parameters(resultParams);
        } catch (Exception ex) {
            throw new CalculationException(ex.getMessage() != null ? ex.getMessage() : "Unknown error during calculation", ex);
        }
    }

    public pl.psi.aaas.communication.Parameter toCommParams(Parameter<?> value) {
        if (!(value instanceof Vector)) {
            throw new CalculationException("Unknown value type " + value.getClazz());
        }
        Vector<?> vector = (Vector<?>) value;
        pl.psi.aaas.communication.Vector commVector = toCommVector(vector);

        return pl.psi.aaas.communication.Parameter.newBuilder()
                .setVector(commVector)
                .build();
    }

    private pl.psi.aaas.communication.Vector toCommVector(Vector<?> vector) {
        pl.psi.aaas.communication.Vector.Builder vectorBuilder = pl.psi.aaas.communication.Vector.newBuilder();
        Class<?> elemClazz = vector.getElemClazz();

        if (elemClazz == Double.class || elemClazz == double.class) {
            toDoubleCommVector((Vector<Double>) vector, vectorBuilder);
        } else if (elemClazz == Long.class || elemClazz == long.class) {
            toLongCommVector((Vector<Long>) vector, vectorBuilder);
        } else if (elemClazz == String.class) {
            toStringCommVector((Vector<String>) vector, vectorBuilder);
        } else if (elemClazz == Boolean.class || elemClazz == boolean.class) {
            toBooleanCommVector((Vector<Boolean>) vector, vectorBuilder);
        } else if (elemClazz == Vector.class) {
            toVectorCommVector((Vector<Vector<?>>) vector, vectorBuilder);
        } else if (elemClazz == ZonedDateTime.class) {
            toZonedDateTimeCommVector((Vector<ZonedDateTime>) vector, vectorBuilder);
        } else {
            throw new CalculationException("Unsupported type: " + elemClazz);
        }
        return vectorBuilder.build();
    }

    private void toDoubleCommVector(Vector<Double> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<DoubleValue> iterable = vector.getValue().stream().map(it -> {
            DoubleValue.Builder builder = DoubleValue.newBuilder();
            if (it == null) {
                builder.setEmpty(EmptyValue.getDefaultInstance());
            } else {
                builder.setValue(it);
            }
            return builder.build();
        }).collect(Collectors.toList());
        vectorBuilder.setDoubleValue(DoubleVector.newBuilder().addAllVector(iterable).build());
    }

    private void toLongCommVector(Vector<Long> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<LongValue> iterable = vector.getValue().stream().map(it -> {
            LongValue.Builder builder = LongValue.newBuilder();
            if (it == null) {
                builder.setEmpty(EmptyValue.getDefaultInstance());
            } else {
                builder.setValue(it);
            }
            return builder.build();
        }).collect(Collectors.toList());
        vectorBuilder.setLongValue(LongVector.newBuilder().addAllVector(iterable).build());
    }

    private void toStringCommVector(Vector<String> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<StringValue> iterable = vector.getValue().stream().map(it -> {
            StringValue.Builder builder = StringValue.newBuilder();
            if (it == null) {
                builder.setEmpty(EmptyValue.getDefaultInstance());
            } else {
                builder.setValue(it);
            }
            return builder.build();
        }).collect(Collectors.toList());
        vectorBuilder.setStringValue(StringVector.newBuilder().addAllVector(iterable).build());
    }

    private void toBooleanCommVector(Vector<Boolean> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<BoolValue> iterable = vector.getValue().stream().map(it -> {
            BoolValue.Builder builder = BoolValue.newBuilder();
            if (it == null) {
                builder.setEmpty(EmptyValue.getDefaultInstance());
            } else {
                builder.setValue(it);
            }
            return builder.build();
        }).collect(Collectors.toList());
        vectorBuilder.setBoolValue(BoolVector.newBuilder().addAllVector(iterable).build());
    }

    private void toZonedDateTimeCommVector(Vector<ZonedDateTime> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<TimestampValue> iterable = vector.getValue().stream().map(it -> {
            TimestampValue.Builder builder = TimestampValue.newBuilder();
            if (it == null) {
                builder.setEmpty(EmptyValue.getDefaultInstance());
            } else {
                builder.setValue(it.toInstant().toEpochMilli());
            }
            return builder.build();
        }).collect(Collectors.toList());
        vectorBuilder.setTimestampValue(TimestampVector.newBuilder().addAllVector(iterable).build());
    }

    private void toVectorCommVector(Vector<Vector<?>> vector, pl.psi.aaas.communication.Vector.Builder vectorBuilder) {
        Iterable<pl.psi.aaas.communication.Vector> iterable = vector.getValue().stream().map(it -> {
            if (it == null) {
                return pl.psi.aaas.communication.Vector.newBuilder().build();
            } else {
                return toCommVector(it);
            }
        }).collect(Collectors.toList());
        vectorBuilder.setVectorValue(VectorVector.newBuilder().addAllVector(iterable).build());
    }

    public Vector<?> fromCommParams(pl.psi.aaas.communication.Vector vector, Parameter<?> parameter) {
        if (parameter instanceof Vector) {
            if (vector.hasDoubleValue()) return fromCommDoubleVector(vector);
            if (vector.hasLongValue()) return fromCommLongVector(vector);
            if (vector.hasBoolValue()) return fromCommBoolVector(vector);
            if (vector.hasStringValue()) return fromCommStringVector(vector);
            if (vector.hasTimestampValue()) return fromCommTimestampVector(vector);
            if (vector.hasEmptyValue()) return (Vector<?>) parameter;
            // TODO: handle vector value if needed
            throw new CalculationException("Unsupported vector type: " + vector);
        } else {
            throw new CalculationException("Unsupported operation, parameter is null or it's not a Vector<?> ");
        }
    }

    private Vector<ZonedDateTime> fromCommTimestampVector(pl.psi.aaas.communication.Vector vector) {
        ZonedDateTime[] array = vector.getTimestampValue().getVectorList().stream().map(it -> {
            if (it.hasEmpty()) return null;
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.getValue()), ZoneOffset.UTC);
        }).toArray(ZonedDateTime[]::new);
        return Parameter.ofArray(array);
    }

    private Vector<String> fromCommStringVector(pl.psi.aaas.communication.Vector vector) {
        String[] array = vector.getStringValue().getVectorList().stream().map(it -> {
            if (it.hasEmpty()) return null;
            return it.getValue();
        }).toArray(String[]::new);
        return Parameter.ofArray(array);
    }

    private Vector<Boolean> fromCommBoolVector(pl.psi.aaas.communication.Vector vector) {
        Boolean[] array = vector.getBoolValue().getVectorList().stream().map(it -> {
            if (it.hasEmpty()) return null;
            return it.getValue();
        }).toArray(Boolean[]::new);
        return Parameter.ofArray(array);
    }

    private Vector<Long> fromCommLongVector(pl.psi.aaas.communication.Vector vector) {
        Long[] array = vector.getLongValue().getVectorList().stream().map(it -> {
            if (it.hasEmpty()) return null;
            return it.getValue();
        }).toArray(Long[]::new);
        return Parameter.ofArray(array);
    }

    private Vector<Double> fromCommDoubleVector(pl.psi.aaas.communication.Vector vector) {
        Double[] array = vector.getDoubleValue().getVectorList().stream().map(it -> {
            if (it.hasEmpty()) return null;
            return it.getValue();
        }).toArray(Double[]::new);
        return Parameter.ofArray(array);
    }
}
