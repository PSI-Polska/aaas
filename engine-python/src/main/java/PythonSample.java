import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.communication.AAASGrpc;
import pl.psi.aaas.engine.python.ConnectionProvider;
import pl.psi.aaas.engine.python.GrpcEngine;
import pl.psi.aaas.usecase.CalculationDefinition;
import pl.psi.aaas.usecase.Parameters;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class PythonSample {
    public static void main(String[] args) {
        Parameter<String> desc = Parameter.ofArray(new String[]{"PTA1A", "PTA1B"});
        Parameter<ZonedDateTime> ts = Parameter.ofArray(new ZonedDateTime[]{ZonedDateTime.now(), ZonedDateTime.now()});
        Parameter<Long> res = Parameter.ofArray(new Long[]{200L, 200L, null}, Long.class);
        Parameter<Long> element = Parameter.ofArray(new Long[]{1L, 2L});
        Parameter<Parameter<?>> vals = Parameter.ofArray(new Parameter<?>[]{element, element});

        Parameter<String> infDesc = Parameter.ofArray(new String[]{"TI1A"});
        Parameter<ZonedDateTime> changeTs = Parameter.ofArray(new ZonedDateTime[]{ZonedDateTime.now()});
        Parameter<Long> state = Parameter.ofArray(new Long[]{1L});

        Map<String, Parameter<?>> inParameters = new HashMap<>();
        inParameters.put("pressureDescriptor", desc);
        inParameters.put("begTimestamp", ts);
        inParameters.put("resolution", res);
        inParameters.put("values", vals);
        inParameters.put("infrastructureDescriptor", infDesc);
        inParameters.put("changeTimestamp", changeTs);
        inParameters.put("state", state);

        CalculationDefinition calcDef = new CalculationDefinition("echo", inParameters, new HashMap<>());

        Parameters result = new GrpcEngine(new FixedConnectionProvider()).call(calcDef);
        System.out.println(result);
    }

    static class FixedConnectionProvider implements ConnectionProvider<AAASGrpc.AAASBlockingStub> {
        @Override
        public AAASGrpc.AAASBlockingStub getConnection() {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 20304)
                    .usePlaintext()
                    .build();
            return AAASGrpc.newBlockingStub(channel);
        }
    }
}
