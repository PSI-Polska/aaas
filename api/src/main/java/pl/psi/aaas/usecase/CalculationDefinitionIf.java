package pl.psi.aaas.usecase;

import pl.psi.aaas.Parameter;

/**
 * The most basic calculation definition.
 */
public interface CalculationDefinitionIf
{
    String getCalculationScript();
    java.util.Map<String, Parameter<?>> getInParameters();
    java.util.Map<String, Parameter<?>> getOutParameters();
}
