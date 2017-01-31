package cz.blahami2.utils.configuration.sources;

import cz.blahami2.utils.configuration.ConfigurationSource;
import cz.blahami2.utils.configuration.utils.ArgumentParser;
import org.junit.Before;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class MapConfigurationSourceTest extends ConfigurationSourceImplTest {

    @Override
    public ConfigurationSource getConfig( Map<String, String> data ) {
        String[] args = new String[]{ "a" };
        ArgumentParser argumentParser = mock( ArgumentParser.class );
        when( argumentParser.parse( args ) ).thenReturn( data );
        MapConfigurationSource config = new MapConfigurationSource( data );
        return config;
    }
}