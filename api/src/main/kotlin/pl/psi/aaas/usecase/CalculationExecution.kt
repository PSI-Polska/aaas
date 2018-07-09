package pl.psi.aaas.usecase

import pl.psi.aaas.Parameter

typealias Symbol = String
typealias Parameters = Map<Symbol, Parameter<*>>
typealias OutParameters = Map<Symbol, Class<Any>> // TODO

/**
 * The most basic calculation definition.
 */
interface CalculationDefinition {
    val calculationScript: String
    val inParameters: Parameters
    val outParameters: Parameters
// TODO add engineDefinition? engineQuery?
}

data class CalculationDefinitionDTO(override val calculationScript: String,
                                    override val inParameters: Parameters = emptyMap(),
                                    override val outParameters: Parameters = emptyMap())
    : CalculationDefinition

/**
 * Calculation Exception.
 *
 * @property message mandatory not empty message
 * @property cause optional cause
 */
open class CalculationException(override val message: String, override val cause: Throwable? = null) : RuntimeException(message, cause)

