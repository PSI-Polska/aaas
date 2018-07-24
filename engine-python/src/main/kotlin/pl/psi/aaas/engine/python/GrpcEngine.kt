package pl.psi.aaas.engine.python

import pl.psi.aaas.Engine
import pl.psi.aaas.Parameter
import pl.psi.aaas.Vector
import pl.psi.aaas.communication.*
import pl.psi.aaas.usecase.CalculationDefinition
import pl.psi.aaas.usecase.CalculationException
import pl.psi.aaas.usecase.Parameters
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import pl.psi.aaas.communication.Parameter as ParameterComm
import pl.psi.aaas.communication.Parameters as ParametersComm
import pl.psi.aaas.communication.Vector as VectorComm

class GrpcEngine(val connectionProvider: ConnectionProvider<AAASGrpc.AAASBlockingStub>) : Engine<CalculationDefinition, Parameters, Parameters> {

    override fun call(calcDef: CalculationDefinition): Parameters? {
        val mappedParams = calcDef.inParameters.map { it.key to toCommParams(it.value) }.toMap()
        val paramsToBeSent = ParametersComm.newBuilder()
                .putAllValue(mappedParams)
                .build()

        val client = connectionProvider.getConnection()

        val callParams = CallParameters.newBuilder()
                .setParams(paramsToBeSent)
                .setScript(calcDef.calculationScript)
                .build()
        try {
            val retParams = client.call(callParams)
            if (retParams.errorMessage.isNotEmpty())
                throw CalculationException(retParams.errorMessage)
            return retParams.params.valueMap.map { it.key to fromCommParams(it.value.vector) }.toMap()
        } catch (ex: Exception) {
            throw CalculationException(ex.message ?: "Unknown error during calculation", ex)
        }
    }


    fun toCommParams(value: Parameter<*>): ParameterComm {
        val vector = when (value) {
            is Vector<*> -> toCommVector(value)
            else         -> throw CalculationException("Unknown value type ${value.clazz}")
        }

        val paramBuilder = ParameterComm.newBuilder()
        return paramBuilder.setVector(vector).build()
    }

    private fun toCommVector(vector: Vector<*>): VectorComm {
        val vectorBuilder = VectorComm.newBuilder()
        when (vector.elemClazz) {
            Double::class.java            -> toDoubleCommVector(vector, vectorBuilder)
            java.lang.Double::class.java  -> toDoubleCommVector(vector, vectorBuilder)
            Long::class.java              -> toLongCommVector(vector, vectorBuilder)
            java.lang.Long::class.java    -> toLongCommVector(vector, vectorBuilder)
            String::class.java            -> toStringCommVector(vector, vectorBuilder)
            java.lang.String::class.java  -> toStringCommVector(vector, vectorBuilder)
            Boolean::class.java           -> toBooleanCommVector(vector, vectorBuilder)
            java.lang.Boolean::class.java -> toBooleanCommVector(vector, vectorBuilder)
            Vector::class.java            -> toVectorCommVector(vector, vectorBuilder)
            ZonedDateTime::class.java     -> toZonedDateTimeCommVector(vector, vectorBuilder)
            else                          -> throw CalculationException("Unsupported type: " + vector.elemClazz)
        }
        return vectorBuilder.build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toDoubleCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<Double?>
        val iterable = vector.value.map {
            with(DoubleValue.newBuilder()) {
                if (it == null)
                    setEmpty(EmptyValue.getDefaultInstance())
                else
                    setValue(it)
            }.build()
        }.asIterable()
        vectorBuilder.doubleValue = DoubleVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toLongCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<Long?>
        val iterable = vector.value.map {
            with(LongValue.newBuilder()) {
                if (it == null)
                    setEmpty(EmptyValue.getDefaultInstance())
                else
                    setValue(it)
            }.build()
        }.asIterable()
        vectorBuilder.longValue = LongVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toStringCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<String?>
        val iterable = vector.value.map {
            with(StringValue.newBuilder()) {
                if (it == null)
                    setEmpty(EmptyValue.getDefaultInstance())
                else
                    setValue(it)
            }.build()
        }.asIterable()
        vectorBuilder.stringValue = StringVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toBooleanCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<Boolean?>
        val iterable = vector.value.map {
            with(BoolValue.newBuilder()) {
                if (it == null)
                    setEmpty(EmptyValue.getDefaultInstance())
                else
                    setValue(it)
            }.build()
        }.asIterable()
        vectorBuilder.boolValue = BoolVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toZonedDateTimeCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<ZonedDateTime?>
        val iterable = vector.value.map {
            with(TimestampValue.newBuilder()) {
                if (it == null)
                    setEmpty(EmptyValue.getDefaultInstance())
                else
                    setValue(it.toInstant().toEpochMilli())
            }.build()
        }.asIterable()
        vectorBuilder.timestampValue = TimestampVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toVectorCommVector(vector: Vector<*>, vectorBuilder: VectorComm.Builder) {
        vector as Vector<Vector<Any>?>
        val iterable = vector.value.map {
            if (it == null)
                VectorComm.newBuilder().build()
            else
                toCommVector(it)
        }.asIterable()
        vectorBuilder.vectorValue = VectorVector.newBuilder().addAllVector(iterable).build()
    }

    @Suppress("UNCHECKED_CAST")
    fun fromCommParams(vector: VectorComm): Vector<Any> =
            if (vector.hasDoubleValue()) fromCommDoubleVector(vector)
            else if (vector.hasLongValue()) fromCommLongVector(vector)
            else if (vector.hasBoolValue()) fromCommBoolVector(vector)
            else if (vector.hasStringValue()) fromCommStringVector(vector)
            else if (vector.hasTimestampValue()) fromCommTimestampVector(vector)
            else if (vector.hasVectorValue()) {
                val array = vector.vectorValue.vectorList.map { fromCommParams(it) }.toTypedArray()
                Parameter.ofArray(array) as Vector<Any>
            } else
                throw CalculationException("Unsupported vector type: " + vector)

    @Suppress("UNCHECKED_CAST")
    private fun fromCommTimestampVector(vector: VectorComm): Vector<Any> {
        val array = vector.timestampValue.vectorList.map {
            if (it.hasEmpty())
                null
            else
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.value), ZoneOffset.UTC)
        }.toTypedArray()
        return Parameter.ofArray(array) as Vector<Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCommStringVector(vector: VectorComm): Vector<Any> {
        val array = vector.stringValue.vectorList.map {
            if (it.hasEmpty())
                null
            else
                it.value
        }.toTypedArray()
        return Parameter.ofArray(array) as Vector<Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCommBoolVector(vector: VectorComm): Vector<Any> {
        val array = vector.boolValue.vectorList.map {
            if (it.hasEmpty())
                null
            else
                it.value
        }.toTypedArray()
        return Parameter.ofArray(array) as Vector<Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCommLongVector(vector: VectorComm): Vector<Any> {
        val array = vector.longValue.vectorList.map {
            if (it.hasEmpty())
                null
            else
                it.value
        }.toTypedArray()
        return Parameter.ofArray(array) as Vector<Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCommDoubleVector(vector: pl.psi.aaas.communication.Vector): Vector<Any> {
        val array = vector.doubleValue.vectorList.map {
            if (it.hasEmpty())
                null
            else
                it.value
        }.toTypedArray()
        return Parameter.ofArray(array) as Vector<Any>
    }
}
