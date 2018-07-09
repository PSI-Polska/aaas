package pl.psi.aaas

import pl.psi.aaas.usecase.CalculationDefinition

/**
 *
 */
interface Facade<in T : CalculationDefinition, R : Any> {
    fun callScript(calcDef: T): R
}
