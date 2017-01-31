package cz.blahami2.utils.configuration.sources;

import cz.blahami2.utils.configuration.ConfigurationSource;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class PropertiesConfigurationSource implements ConfigurationSource {

    private final Properties properties;

    public PropertiesConfigurationSource( Properties properties ) {
        this.properties = properties;
    }

    @Override
    public Optional<String> getValue( String key ) {
        return Optional.ofNullable( properties.getProperty( key ) );
    }

    @Override
    public <T> Optional<T> getValue( String key, Class<T> clazz ) {
        return ConfigurationSource.CommonMappers.map( clazz, properties.getProperty( key )  );
    }

    @Override
    public <T> Optional<T> getValue( String key, Function<String, T> mapper ) {
        return getValue( key ).map( mapper );
    }
}
