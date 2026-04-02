package pl.psi.aaas.usecase;

import pl.psi.aaas.Parameter;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * This class represents a detailed definition for performing calculations. It provides inputs, outputs,
 * configured parameters, and any relevant metadata required to execute a calculation process.
 * The class is immutable for most fields, ensuring thread safety and consistency in shared environments,
 * while supporting configurable input and output parameters.
 * Implements the {@link CalculationDefinitionIf} interface, which ensures compatibility with core calculation
 * behaviors.
 */
public class CalculationDefinition implements CalculationDefinitionIf
{
    private final Map< String, Long > timeSeriesIdsIn;
    private final Map< String, Long > timeSeriesIdsOut;
    private final ZonedDateTime begin;
    private final ZonedDateTime end;
    private final String calculationScript;
    private Map< String, Parameter< ? > > inParameters = new HashMap<>();
    private Map< String, Parameter< ? > > outParameters = new HashMap<>();

    public CalculationDefinition( Map< String, Long > timeSeriesIdsIn, Map< String, Long > timeSeriesIdsOut,
        ZonedDateTime begin, ZonedDateTime end, String calculationScript )
    {
        this.timeSeriesIdsIn = timeSeriesIdsIn;
        this.timeSeriesIdsOut = timeSeriesIdsOut;
        this.begin = begin;
        this.end = end;
        this.calculationScript = calculationScript;
    }

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

    @Override
    public Map< String, Parameter< ? > > getInParameters()
    {
        return inParameters;
    }

    public void setInParameters( Map< String, Parameter< ? > > inParameters )
    {
        this.inParameters = inParameters;
    }

    @Override
    public Map< String, Parameter< ? > > getOutParameters()
    {
        return outParameters;
    }

    public void setOutParameters( Map< String, Parameter< ? > > outParameters )
    {
        this.outParameters = outParameters;
    }
}
