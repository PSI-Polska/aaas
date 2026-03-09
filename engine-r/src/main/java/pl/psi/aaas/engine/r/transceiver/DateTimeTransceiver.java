package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
class DateTimeTransceiver
    implements RValuesTransceiver< Parameter< ZonedDateTime >, ZonedDateTime, CalculationDefinitionIf >
{
    private final RConnection session;

    public DateTimeTransceiver( RConnection session )
    {
        this.session = session;
    }

    @Override
    public RConnection getSession()
    {
        return session;
    }

    @Override
    public void send( String name, Parameter< ZonedDateTime > value, CalculationDefinitionIf definition )
        throws CalculationException
    {
        try
        {
            long epochSecond = value.getValue()
                .toEpochSecond();
            session.assign( name, new REXPDouble( (double)epochSecond ) );
            session.voidEval( name + " <- structure(" + name + ", class=c('POSIXt','POSIXct'))" );
            session.voidEval( "attr(" + name + ", \"tzone\") <- \"UTC\"" );
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error sending ZonedDateTime to R", e );
        }
    }

    @Override
    public ZonedDateTime receive( String name, Object result, CalculationDefinitionIf definition )
        throws CalculationException
    {
        try
        {
            REXP rexpResult = session.get( name, null, true );
            if( rexpResult == null || rexpResult.isNull() )
                return null;
            long epochSecond = (long)rexpResult.asDouble();
            return ZonedDateTime.ofInstant( Instant.ofEpochSecond( epochSecond ), ZoneOffset.UTC );
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error receiving ZonedDateTime from R", e );
        }
    }
}
