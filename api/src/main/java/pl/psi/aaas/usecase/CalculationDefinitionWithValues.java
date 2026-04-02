package pl.psi.aaas.usecase;

import java.util.List;
import java.util.Map;

/**
 * Represents a detailed wrapper for a calculation definition and its associated input time-series values.
 * This class is immutable and ensures thread safety. It encapsulates a {@link CalculationDefinitionIf} that
 * defines the calculation logic and metadata, along with a list of time-series input values where each entry
 * is a mapping of a string key to an array of doubles.
 */
public final class CalculationDefinitionWithValues
{
    private final CalculationDefinitionIf calculationDefinition;
    private final List< Map.Entry< String, Double[] > > timeSeriesValuesIn;

    public CalculationDefinitionWithValues( CalculationDefinitionIf calculationDefinition,
        List< Map.Entry< String, Double[] > > timeSeriesValuesIn )
    {
        this.calculationDefinition = calculationDefinition;
        this.timeSeriesValuesIn = timeSeriesValuesIn;
    }

    public CalculationDefinitionIf getCalculationDefinition()
    {
        return calculationDefinition;
    }

    public java.util.List< java.util.Map.Entry< String, Double[] > > getTimeSeriesValuesIn()
    {
        return timeSeriesValuesIn;
    }
}
