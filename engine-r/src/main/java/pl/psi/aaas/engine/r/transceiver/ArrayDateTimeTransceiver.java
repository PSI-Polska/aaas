package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * The ArrayDateTimeTransceiver class is responsible for transmitting and receiving arrays of ZonedDateTime
 * values to and from an R session. It converts Java time representations into a format compatible with R and
 * vice versa, while maintaining the integrity of timezone and null values.
 * This class implements the RValuesTransceiver interface for handling the transmission of
 * Parameter<ZonedDateTime[]> values and their interaction with calculations defined by
 * CalculationDefinitionIf.
 */
class ArrayDateTimeTransceiver
    implements RValuesTransceiver< Parameter< ZonedDateTime[] >, ZonedDateTime[], CalculationDefinitionIf >
{
    private final RConnection session;

    public ArrayDateTimeTransceiver( RConnection session )
    {
        this.session = session;
    }

    @Override
    public RConnection getSession()
    {
        return session;
    }

    @Override
    public void send( String name, Parameter< ZonedDateTime[] > value, CalculationDefinitionIf definition )
        throws CalculationException
    {
        try
        {
            double[] epochSeconds = Arrays.stream( value.getValue() )
                .mapToDouble( dt -> dt != null ? (double)dt.toEpochSecond() : REXPDouble.NA )
                .toArray();

            session.assign( name, new REXPDouble( epochSeconds ) );
            session.voidEval( name + " <- structure(" + name + ", class=c('POSIXt','POSIXct'))" );
            session.voidEval( "attr(" + name + ", \"tzone\") <- \"UTC\"" );
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error sending ZonedDateTime array to R", e );
        }
    }

    @Override
    public ZonedDateTime[] receive( String name, Object result, CalculationDefinitionIf definition )
        throws CalculationException
    {
        try
        {
            REXP rexpResult;
            if( result instanceof REXP )
            {
                rexpResult = (REXP)result;
            }
            else
            {
                rexpResult = session.get( name, null, true );
            }

            if( rexpResult == null || rexpResult.isNull() )
                return null;

            double[] doubles = rexpResult.asDoubles();
            ZonedDateTime[] resultArr = new ZonedDateTime[ doubles.length ];
            for( int i = 0; i < doubles.length; i++ )
            {
                if( REXPDouble.isNA( doubles[ i ] ) )
                {
                    resultArr[ i ] = null;
                }
                else
                {
                    resultArr[ i ] = ZonedDateTime.ofInstant( Instant.ofEpochSecond( (long)doubles[ i ] ),
                        ZoneOffset.UTC );
                }
            }
            return resultArr;
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error receiving ZonedDateTime array from R", e );
        }
        catch( REXPMismatchException e )
        {
            throw new CalculationException( "Error converting to Double from RServe result", e );
        }
        catch( REngineException e )
        {
            throw new CalculationException( "Error obtaining R session.", e );
        }
    }
}
