package pl.psi.aaas.usecase;

/**
 * Calculation Exception.
 */
public class CalculationException extends RuntimeException
{
    public CalculationException( String message )
    {
        super( message );
    }

    public CalculationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
