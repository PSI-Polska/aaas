package pl.psi.aaas.engine.r

import org.rosuda.REngine.Rserve.RConnection
import org.rosuda.REngine.Rserve.RserveException
import org.slf4j.LoggerFactory
import pl.psi.aaas.Engine
import pl.psi.aaas.engine.r.RServeEngine.Companion.baseUserScriptPath
import pl.psi.aaas.engine.r.RServeEngine.Companion.log
import pl.psi.aaas.engine.r.transceiver.RValuesTransceiverFactory
import pl.psi.aaas.usecase.CalculationDefinition
import pl.psi.aaas.usecase.CalculationException
import pl.psi.aaas.usecase.Parameters

/**
 * TODO
 */
class RServeEngine<in D : CalculationDefinition, V, out R>(private val connectionProvider: RConnectionProvider) : Engine<D, V, R> {
    companion object {
        internal val log = LoggerFactory.getLogger(RServeEngine::class.java)
        internal var baseUserScriptPath = "/var/userScripts/"
    }

    // TODO 05.05.2018 kskitek: introduce a way to register and select proper transceiver
    // maybe extend the factory?
    override fun call(calcDef: D): R? =
            try {
                val conn = connectionProvider.getConnection()
                log.debug("Evaluating $calcDef")

                calcDef.sourceScript(conn)

                // TODO we can remove the above line - use only inParameters?
                calcDef.inParameters.forEach {
                    val t = RValuesTransceiverFactory.get(it.value, conn)
                    t.send(it.key, it.value, calcDef)
                }

                debugR(calcDef.inParameters, conn)

                log.debug("Calling script")
                val result = conn.eval("dfOut <- run(dfIn, inParameters)")

                val retMap = calcDef.outParameters.map { it.key to RValuesTransceiverFactory.get(it.value, conn) }
                        .map { it.first to it.second.receive(it.first, null, calcDef) }.toMap()
                log.debug(retMap.entries.joinToString("\n"))

                null
            } catch (ex: RserveException) {
                ex.printStackTrace()
                throw CalculationException(ex.message ?: "There was an error during calculation.")
            }

    private fun debugR(parameters: Parameters, conn: RConnection) =
            parameters.map { it.key }
                    .forEach {
                        conn.voidEval("print(\"## $it\")")
                        conn.voidEval("str($it)")
                    }
}

private fun CalculationDefinition.sourceScript(conn: RConnection) {
    val path = """$baseUserScriptPath$calculationScript.R"""
    log.debug("""Sourcing: $path""")
    conn.voidEval("""writeLines("##\nStarted execution ofPrimitive: $path\n##")""")
    conn.voidEval("""source("$path")""")
}

