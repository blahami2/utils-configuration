package cz.blahami2.utils.configuration;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface ConfigurationSource {

    Optional<String> getValue( String key );

    /**
     * Supports only basic types (primitive wrappers). For custom mapping, use getValue(String, Function) instead
     *
     * @param key   key
     * @param clazz return type
     * @param <T>   return type
     * @return value for the given key
     */
    <T> Optional<T> getValue( String key, Class<T> clazz );

    default <T> Optional<T> getValue( String key, Function<String, T> mapper ) {
        return getValue( key ).map( mapper );
    }

    default boolean hasValue( String key ) {
        return getValue( key ).isPresent();
    }

    class CommonMappers {
        private static Map<Class, Function> mapperMap = new HashMap<Class, Function>() {{
            put( Boolean.class, ( str ) -> Boolean.parseBoolean( str.toString() ) );
            put( Character.class, ( str ) -> {
                if ( str.toString().length() <= 0 || 1 < str.toString().length() ) {
                    throw new IllegalArgumentException( "String cannot be parsed into char: '" + str.toString() + "'" );
                }
                return str.toString().charAt( 0 );
            } );
            put( Short.class, ( str ) -> Short.parseShort( str.toString() ) );
            put( Integer.class, ( str ) -> Integer.parseInt( str.toString() ) );
            put( Long.class, ( str ) -> Long.parseLong( str.toString() ) );
            put( Float.class, ( str ) -> Float.parseFloat( str.toString() ) );
            put( Double.class, ( str ) -> Double.parseDouble( str.toString() ) );
        }};

        public static <T> Optional<T> map( Class<T> clazz, Object value ) {
            return Optional.ofNullable( value )
                    .map(
                            Optional.ofNullable( mapperMap.get( clazz ) )
                                    .orElseThrow( () -> new IllegalArgumentException( "Unknown class: " + clazz.getName() + ", use mapper instead." ) )
                    );
        }
    }
}
