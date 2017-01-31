package cz.blahami2.utils.configuration;


import cz.blahami2.utils.configuration.sources.ConfigurationSourceImplTest;
import cz.blahami2.utils.configuration.sources.MapConfigurationSource;
import org.junit.Before;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ConfigurationTest extends ConfigurationSourceImplTest {

    @Override
    public ConfigurationSource getConfig( Map<String, String> data ) {
        List<ConfigurationSource> sources = Arrays.asList(
                new MapConfigurationSource( data ),
                new MapConfigurationSource( data )
        );
        return new Configuration( sources );
    }
}