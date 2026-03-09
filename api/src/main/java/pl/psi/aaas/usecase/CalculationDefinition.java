package pl.psi.aaas.usecase;

import java.util.Map;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public record CalculationDefinition(Map< String, Long > timeSeriesIdsIn,
                                    Map< String, Long > timeSeriesIdsOut, java.time.ZonedDateTime begin,
                                    java.time.ZonedDateTime end, String calculationScript)
    implements CalculationDefinitionIf
{
    @Override
    public Map< String, Long > getTimeSeriesIdsIn()
    {
        return timeSeriesIdsIn;
    }

    @Override
    public Map< String, Long > getTimeSeriesIdsOut()
    {
        return timeSeriesIdsOut;
    }

    @Override
    public java.time.ZonedDateTime getBegin()
    {
        return begin;
    }

    @Override
    public java.time.ZonedDateTime getEnd()
    {
        return end;
    }

    @Override
    public String getCalculationScript()
    {
        return calculationScript;
    }
}
