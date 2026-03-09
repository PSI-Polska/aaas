package pl.psi.aaas.usecase;

import java.util.Map;

/**
 * The most basic calculation definition.
 */
public interface CalculationDefinitionIf
{
    Map< String, Long > getTimeSeriesIdsIn();

    Map< String, Long > getTimeSeriesIdsOut();

    java.time.ZonedDateTime getBegin();

    java.time.ZonedDateTime getEnd();

    String getCalculationScript();
}
