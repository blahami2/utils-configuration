package cz.blahami2.utils.configuration.sources;

import cz.blahami2.utils.configuration.ConfigurationSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class MapConfigurationSource implements ConfigurationSource {

    private final Map<String, String> map;

    public MapConfigurationSource( Map<String, String> map ) {
        this.map = new HashMap<>( map );
    }

    @Override
    public Optional<String> getValue( String key ) {
        return Optional.ofNullable( map.get( key ) );
    }

    @Override
    public <T> Optional<T> getValue( String key, Class<T> clazz ) {
        return ConfigurationSource.CommonMappers.map( clazz, map.get( key ) );
    }

    @Override
    public <T> Optional<T> getValue( String key, Function<String, T> mapper ) {
        return getValue( key ).map( mapper );
    }
}
