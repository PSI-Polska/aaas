package pl.psi.aaas

import pl.psi.aaas.usecase.CalculationDefinition
import pl.psi.aaas.usecase.CalculationDefinitionDTO
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/call")
class RestEndpoint @Inject constructor(private val facade: Facade<CalculationDefinition, Unit>) {

    @GET
    fun callDef() {
        facade.callScript(getDefinition())
    }

    fun getDefinition(): CalculationDefinition {
        val strArr = arrayOf("a", "b", "c")
        val dtArr = arrayOf(ZonedDateTime.now().minusHours(2), ZonedDateTime.now().minusHours(1), ZonedDateTime.now())
        val strVec = Parameter.ofArrayNotNull(strArr, String::class.java)
        val dtVec = Parameter.ofArrayNotNull(dtArr, ZonedDateTime::class.java)
        val longVec = Parameter.ofArrayNotNull(arrayOf(1, 2, 3L), Long::class.java)
        val doubleVec = Parameter.ofArrayNotNull(arrayOf(0.1, 0.2, 1.0), Double::class.java)
        val doubleNullVec = Parameter.ofArray(arrayOf(0.1, null, 1.0), Double::class.java)
        val boolVec = Parameter.ofArrayNotNull(arrayOf(true, false, true), Boolean::class.java)
        val boolNullVec = Parameter.ofArray(arrayOf(true, false, null), Boolean::class.java)
        val dfColumns = arrayOf(Column("dt", dtVec as Vector<Any>), Column("longs", longVec as Vector<Any>), Column("doubles", doubleVec as Vector<Any>))

        val parameters = mutableMapOf(
                "str" to Parameter.ofPrimitive("str_value")
                , "dt" to Parameter.ofPrimitive(ZonedDateTime.now())
                , "d" to Parameter.ofPrimitive(0.75)
                , "l" to Parameter.ofPrimitive(10L)
                , "b" to Parameter.ofPrimitive(false)
                , "strV" to strVec
                , "dtV" to dtVec
                , "longV" to longVec
                , "doubleV" to doubleVec
                , "doubleNullV" to doubleNullVec
                , "boolV" to boolVec
                , "boolNullV" to boolNullVec
                , "df" to Parameter.ofDataFrame(dfColumns)
        )

        return CalculationDefinitionDTO("add",
                parameters,
                parameters)
    }
}