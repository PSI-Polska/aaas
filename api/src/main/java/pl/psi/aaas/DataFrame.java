package pl.psi.aaas;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents DataFrame - array of names, heterogeneous {@link Vector}s. Only types supported by
 * {@link Vector} can be used.
 */
public class DataFrame extends Parameter< Column[] >
{
    private final Class< ? >[] columnClasses;

    public DataFrame( Column[] value, Class< ? >[] columnClasses )
    {
        this( value, (Class< Column[] >)value.getClass(), columnClasses );
    }

    public DataFrame( Column[] value, Class< Column[] > clazz, Class< ? >[] columnClasses )
    {
        super( value, clazz );
        this.columnClasses = columnClasses;
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
            .collect( Collectors.toMap( Column::getSymbol, col -> col.getVector()
                .getValue() ) );
    }

    public Class< ? >[] getColumnClasses()
    {
        return columnClasses;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;
        DataFrame dataFrame = (DataFrame)o;
        return Arrays.equals( value, dataFrame.value ) && Objects.equals( clazz, dataFrame.clazz )
            && Arrays.equals( columnClasses, dataFrame.columnClasses );
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash( clazz );
        result = 31 * result + Arrays.hashCode( value );
        result = 31 * result + Arrays.hashCode( columnClasses );
        return result;
    }

    @Override
    public String toString()
    {
        return "DataFrame(value=" + Arrays.toString( value ) + ", clazz=" + clazz + ", columnClasses="
            + Arrays.toString( columnClasses ) + ")";
    }
}
