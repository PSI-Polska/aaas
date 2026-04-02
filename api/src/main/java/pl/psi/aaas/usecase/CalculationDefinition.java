package pl.psi.aaas.usecase;

import java.util.HashMap;
import java.util.Map;

import pl.psi.aaas.Parameter;

/**
 * This class represents a detailed definition for performing calculations. It provides inputs, outputs,
 * configured parameters, and any relevant metadata required to execute a calculation process. The class is
 * immutable for most fields, ensuring thread safety and consistency in shared environments, while supporting
 * configurable input and output parameters. Implements the {@link CalculationDefinitionIf} interface, which
 * ensures compatibility with core calculation behaviors.
 */
public class CalculationDefinition implements CalculationDefinitionIf
{
    private final String calculationScript;
    private Map< String, Parameter< ? > > inParameters = new HashMap<>();
    private Map< String, Parameter< ? > > outParameters = new HashMap<>();

    public CalculationDefinition( String calculationScript )
    {
        this.calculationScript = calculationScript;
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
