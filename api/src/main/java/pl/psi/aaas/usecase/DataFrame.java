package pl.psi.aaas.usecase;

import pl.psi.aaas.Column;
import pl.psi.aaas.Parameter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Very crude implementation of Primitive DataFrame. When moving to Java8 think about moving to external
 * implementation like ch.netzwerg.paleo.DataFrame.
 */
public class DataFrame extends Parameter< Column[] >
{

    public DataFrame( Column[] columns, Class< ? >[] columnClasses )
    {
        super( columns, (Class< Column[] >)columns.getClass() );
    }

    public Object[] get( int row )
    {
        if( row < size() )
        {
            Object[] result = new Object[ value.length ];
            for( int i = 0; i < value.length; i++ )
            {
                result[ i ] = ((Object[])value[ i ].getVector()
                    .getValue())[ row ];
            }
            return result;
        }
        else
        {
            return null;
        }
    }

    public Object[] get( String colName )
    {
        return Arrays.stream( value )
            .filter( col -> col.getSymbol()
                .equals( colName ) )
            .findFirst()
            .map( col -> (Object[])col.getVector()
                .getValue() )
            .orElse( null );
    }

    public Column[] getAll()
    {
        return value;
    }

    public List< String > getColumns()
    {
        return Arrays.stream( value )
            .map( Column::getSymbol )
            .collect( Collectors.toList() );
    }

    public int size()
    {
        if( value.length > 0 )
        {
            return (value[ 0 ].getVector()
                .getValue()).length;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns a view of Primitive the DataFrame with predicate applied to columns.
     */
    public DataFrame getFiltered( Predicate< String > predicate )
    {
        Column[] filteredColumns = Arrays.stream( value )
            .filter( col -> predicate.test( col.getSymbol() ) )
            .toArray( Column[]::new );

        Class< ? >[] columnClasses = Arrays.stream( filteredColumns )
            .map( col -> col.getVector()
                .getElemClazz() )
            .toArray( Class< ? >[]::new );

        return new DataFrame( filteredColumns, columnClasses );
    }

    public Map< String, Object[] > toMap()
    {
        return Arrays.stream( value )
            .collect( Collectors.toMap( Column::getSymbol, col -> (Object[])col.getVector()
                .getValue() ) );
    }
}
