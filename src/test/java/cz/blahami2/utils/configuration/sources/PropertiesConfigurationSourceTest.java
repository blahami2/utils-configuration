package cz.blahami2.utils.configuration.sources;

import cz.blahami2.utils.configuration.ConfigurationSource;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class PropertiesConfigurationSourceTest extends ConfigurationSourceImplTest {

    @Override
    public ConfigurationSource getConfig( Map<String, String> data ) {
        Properties properties = data.entrySet().stream().collect( () -> new Properties(),
                ( p, entry ) -> p.setProperty( entry.getKey(), entry.getValue() ),
                ( p1, p2 ) -> p1.putAll( p2 ) );
        PropertiesConfigurationSource config = new PropertiesConfigurationSource( properties );
        return config;
    }
}