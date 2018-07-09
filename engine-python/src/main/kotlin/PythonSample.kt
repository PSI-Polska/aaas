import io.grpc.ManagedChannelBuilder
import pl.psi.aaas.Parameter
import pl.psi.aaas.communication.AAASGrpc
import pl.psi.aaas.engine.python.ConnectionProvider
import pl.psi.aaas.engine.python.GrpcEngine
import pl.psi.aaas.usecase.CalculationDefinitionDTO
import java.time.ZonedDateTime

object PythonSample {
    @JvmStatic
    fun main(args: Array<String>) {
        val desc = Parameter.ofArray(arrayOf("PTA1A", "PTA1B"))
        val ts = Parameter.ofArray(arrayOf(ZonedDateTime.now(), ZonedDateTime.now()))
        val res = Parameter.ofArray(arrayOf(200L, 200L, null), Long::class.java)
        val element = Parameter.ofArray(arrayOf(1L, 2L))
        val vals = Parameter.ofArray(arrayOf(element, element))

        val infDesc = Parameter.ofArray(arrayOf("TI1A"))
        val changeTs = Parameter.ofArray(arrayOf(ZonedDateTime.now()))
        val state = Parameter.ofArray(arrayOf(1L))

        val inParameters = mapOf("pressureDescriptor" to desc,
                "begTimestamp" to ts,
                "resolution" to res,
                "values" to vals,
                "infrastructureDescriptor" to infDesc,
                "changeTimestamp" to changeTs,
                "state" to state)
        val calcDef = CalculationDefinitionDTO("echo", inParameters)

        val result = GrpcEngine(FixedConnectionProvider()).call(calcDef)
        println(result)
    }
}

class FixedConnectionProvider : ConnectionProvider<AAASGrpc.AAASBlockingStub> {
    override fun getConnection(): AAASGrpc.AAASBlockingStub {
        val channel = ManagedChannelBuilder.forAddress("localhost", 20304)
                .usePlaintext()
                .build()
        return AAASGrpc.newBlockingStub(channel)
    }

}