package pl.psi.aaas

import pl.psi.aaas.usecase.CalculationDefinition
import pl.psi.aaas.usecase.CalculationException

/**
 * The calculation engine implementations are used by use cases implementations ([CalculationExecution]).
 */
interface Engine<in T : CalculationDefinition, V, out R> {
    /**
     * Call the calculation definition on the engine.
     *
     * @param calcDef TimeSeriesWithValuesCalculationDefinition passed to the engine
     *
     * @throws CalculationException
     */
    @Throws(CalculationException::class)
    fun call(calcDef: T): R?
}

interface EngineValuesSender<in V, in D : CalculationDefinition> {

    @Throws(CalculationException::class)
    fun send(name: String, value: V, definition: D)
}

interface EngineValuesReceiver<out R, in D : CalculationDefinition> {
    @Throws(CalculationException::class)
    fun receive(name: String, result: Any?, definition: D): R?
    // TODO 05.05.2018 kskitek: can we have something better than 'Any?'?
}

/**
 * Implementations translate In/Out types ofPrimitive [ValuesRepository] into [Engine]'s native representation.
 *
 * @param V read and save values type parameter
 * @param R read and save values type parameter
 * @param S session shares by [Engine] and EngineValuesTransceiver
 */
// TODO 07.05.2018 kskitek: maybe you don't need V and R while there is D
// TODO 18.05.2018 kskitek: maybe it should be just transformer?!
interface EngineValuesTranceiver<in V, out R, in D : CalculationDefinition, out S> : EngineValuesSender<V, D>, EngineValuesReceiver<R, D> {
    val session: S
    // TODO 10.05.2018 kskitek: this will not be thread safe
}
