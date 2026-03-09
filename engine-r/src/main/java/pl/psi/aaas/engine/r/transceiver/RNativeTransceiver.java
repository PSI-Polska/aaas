package pl.psi.aaas.engine.r.transceiver;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import pl.psi.aaas.Parameter;
import pl.psi.aaas.engine.r.RValuesTransceiver;
import pl.psi.aaas.usecase.CalculationDefinitionIf;
import pl.psi.aaas.usecase.CalculationException;

import java.util.function.Function;

class RNativeTransceiver< V extends Parameter< ? >, R >
    implements RValuesTransceiver< V, R, CalculationDefinitionIf >
{
    private final RConnection session;
    private final Function< V, REXP > outTransformer;
    private final Function< REXP, R > inTransformer;

    public RNativeTransceiver( RConnection session, Function< V, REXP > outTransformer,
        Function< REXP, R > inTransformer )
    {
        this.session = session;
        this.outTransformer = outTransformer;
        this.inTransformer = inTransformer != null ? inTransformer : ( r ) -> null;
    }

    @Override
    public RConnection getSession()
    {
        return session;
    }

    @Override
    public void send( String name, V value, CalculationDefinitionIf definition ) throws CalculationException
    {
        try
        {
            session.assign( name, outTransformer.apply( value ) );
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error assigning variable " + name + " in R", e );
        }
    }

    @Override
    public R receive( String name, Object result, CalculationDefinitionIf definition )
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
            {
                return null;
            }
            return inTransformer.apply( rexpResult );
        }
        catch( RserveException e )
        {
            throw new CalculationException( "Error receiving variable " + name + " from R", e );
        }
        catch( REngineException e )
        {
            throw new CalculationException( "Cannot obtain Rserve session.", e );
        }
    }
}
