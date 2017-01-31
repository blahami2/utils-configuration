package cz.blahami2.utils.configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class Configuration implements ConfigurationSource {
    private final List<ConfigurationSource> sources;

    public Configuration( List<ConfigurationSource> sources ) {
        this.sources = sources;
    }

    @Override
    public Optional<String> getValue( String key ) {
        return sources.stream()
                .filter( source -> source.hasValue( key ) )
                .map( source -> source.getValue( key ) )
                .limit( 1 ).findAny().orElse( Optional.empty() );
    }

    @Override
    public <T> Optional<T> getValue( String key, Class<T> clazz ) {
        return sources.stream()
                .filter( source -> source.hasValue( key ) )
                .map( source -> ConfigurationSource.CommonMappers.map( clazz, source.getValue( key ).get() ) )
                .limit( 1 ).findAny().orElse( Optional.empty() );
    }
}
