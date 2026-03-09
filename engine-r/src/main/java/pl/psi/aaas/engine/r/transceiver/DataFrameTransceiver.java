package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Column;
import pl.psi.aaas.usecase.DataFrame;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.Vector;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class DataFrameTransceiver implements RValuesTransceiver<DataFrame, DataFrame, CalculationDefinitionIf > {
    private final RConnection session;

    public DataFrameTransceiver(RConnection session) {
        this.session = session;
    }

    @Override
    public RConnection getSession() {
        return session;
    }

    private List<Q> findAllTransceivers(DataFrame df) {
        List<Q> result = new ArrayList<>();
        for (Column col : df.getValue()) {
            RValuesTransceiver<Vector<?>, ?, CalculationDefinitionIf > transceiver =
                    (RValuesTransceiver<Vector<?>, ?, CalculationDefinitionIf >) RValuesTransceiverFactory.get(col.getVector(), session);
            result.add(new Q(col.getSymbol(), col.getVector(), transceiver, (Class<Object>) col.getVector().getElemClazz()));
        }
        return result;
    }

    @Override
    public void send(String name, DataFrame value, CalculationDefinitionIf definition) throws CalculationException {
        try {
            List<String> columnNames = IntStream.range(0, value.getValue().length)
                    .mapToObj(i -> value.getValue()[i].getSymbol())
                    .collect(Collectors.toList());

            String columnNamesCSV = columnNames.stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", "));

            List<String> randColNames = IntStream.range(0, value.getValue().length)
                    .mapToObj(i -> name + "Col" + i)
                    .collect(Collectors.toList());

            String randColNamesCSV = String.join(", ", randColNames);

            List<Q> transceivers = findAllTransceivers(value);
            for (int i = 0; i < transceivers.size(); i++) {
                Q q = transceivers.get(i);
                String randName = randColNames.get(i);
                q.transceiver.send(randName, q.vector, definition);
            }

            session.voidEval(name + " <- data.frame(" + randColNamesCSV + ")");
            session.voidEval("names(" + name + ") <- c(" + columnNamesCSV + ")");
        } catch (RserveException e) {
            throw new CalculationException("Error sending DataFrame to R", e);
        }
    }

    @Override
    public DataFrame receive(String name, Object result, CalculationDefinitionIf definition) throws CalculationException {
        try {
            REXP rexpResult = session.get(name, null, true);
            if (rexpResult == null || rexpResult.isNull()) return null;

            Parameter<?> param = definition.getOutParameters().get(name);
            if (!(param instanceof DataFrame)) {
                throw new CalculationException("Expected DataFrame in outParameters for " + name);
            }
            DataFrame df = (DataFrame) param;
            List<Q> transceivers = findAllTransceivers(df);
            RList rList = rexpResult.asList();

            Column[] columns = new Column[transceivers.size()];
            for (int i = 0; i < transceivers.size(); i++) {
                Q q = transceivers.get(i);
                Object receivedValues = q.transceiver.receive(q.symbol, rList.get(q.symbol), definition);
                Vector<Object> vector = Parameter.ofArray((Object[]) receivedValues, q.clazz);
                columns[i] = new Column(q.symbol, vector);
            }

            return Parameter.ofDataFrame(columns);
        } catch (RserveException e) {
            throw new CalculationException("Error receiving DataFrame from R", e);
        }
        catch( REXPMismatchException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static class Q {
        final String symbol;
        final Vector<?> vector;
        final RValuesTransceiver<Vector<?>, ?, CalculationDefinitionIf > transceiver;
        final Class<Object> clazz;

        Q(String symbol, Vector<?> vector, RValuesTransceiver<Vector<?>, ?, CalculationDefinitionIf > transceiver, Class<Object> clazz) {
            this.symbol = symbol;
            this.vector = vector;
            this.transceiver = transceiver;
            this.clazz = clazz;
        }
    }
}
