package pl.psi.aaas.engine.r.transceiver

import org.rosuda.REngine.Rserve.RConnection
import pl.psi.aaas.engine.r.RValuesTransceiver
import pl.psi.aaas.usecase.CalculationDefinition
import pl.psi.aaas.usecase.CalculationException
import pl.psi.aaas.DataFrame
import pl.psi.aaas.Parameter
import pl.psi.aaas.Primitive
import pl.psi.aaas.Vector
import java.time.ZonedDateTime

// TODO 09.05.2018 kskitek: this factory has to be generic in terms ofPrimitive Engine impl; provided separately; registration ofPrimitive impls with SPI
// TODO rather than using instanceof and casting use visitor/handler as a chain of first
internal object RValuesTransceiverFactory {
    fun get(param: Parameter<*>, conn: RConnection): RValuesTransceiver<Parameter<*>, *, CalculationDefinition> =
            when (param) {
                is Primitive -> primitiveTransceiver(param.clazz as Class<Any>, conn)
                is Vector<*> -> vectorTransceiver(param, conn)
                is DataFrame -> DataFrameTransceiver(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            }

    private fun primitiveTransceiver(clazz: Class<Any>, conn: RConnection): RValuesTransceiver<Parameter<*>, *, CalculationDefinition> =
            when (clazz) {
                java.lang.String::class.java  -> RPrimitiveTransceiver.string(conn)
                String::class.java            -> RPrimitiveTransceiver.string(conn)
                java.lang.Long::class.java    -> RPrimitiveTransceiver.long(conn)
                Long::class.java              -> RPrimitiveTransceiver.long(conn)
                java.lang.Double::class.java  -> RPrimitiveTransceiver.double(conn)
                Double::class.java            -> RPrimitiveTransceiver.double(conn)
                java.lang.Boolean::class.java -> RPrimitiveTransceiver.boolean(conn)
                Boolean::class.java           -> RPrimitiveTransceiver.boolean(conn)
                ZonedDateTime::class.java     -> DateTimeTransceiver(conn) as RValuesTransceiver<*, *, *>
                else                          -> throw CalculationException("Not implemented parameter type $clazz")
            } as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
}

private fun vectorTransceiver(param: Vector<*>, conn: RConnection): RValuesTransceiver<Parameter<*>, *, CalculationDefinition> = //EngineValuesSender TODO
        when (param.elemClazz) {
            java.lang.String::class.java  -> RArrayTransceiver.string(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            String::class.java            -> RArrayTransceiver.string(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            java.lang.Long::class.java    -> RArrayTransceiver.long(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            Long::class.java              -> RArrayTransceiver.long(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            java.lang.Double::class.java  -> RArrayTransceiver.double(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            Double::class.java            -> RArrayTransceiver.double(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            java.lang.Boolean::class.java -> RArrayTransceiver.boolean(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            Boolean::class.java           -> RArrayTransceiver.boolean(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            ZonedDateTime::class.java     -> ArrayDateTimeTransceiver(conn) as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>
            else                          -> throw CalculationException("Not implemented array parameter type ${param.elemClazz}")
        } as RValuesTransceiver<Parameter<*>, *, CalculationDefinition>

