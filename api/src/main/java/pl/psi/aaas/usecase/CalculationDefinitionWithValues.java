package pl.psi.aaas.usecase;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public record CalculationDefinitionWithValues(CalculationDefinitionIf calculationDefinitionIf,
                                              java.util.List< java.util.Map.Entry< String, Double[] > > timeSeriesValuesIn)
{
    public CalculationDefinitionIf getCalculationDefinition()
    {
        return calculationDefinitionIf;
    }

    public java.util.List< java.util.Map.Entry< String, Double[] > > getTimeSeriesValuesIn()
    {
        return timeSeriesValuesIn;
    }
}
