package cz.blahami2.utils.configuration;

import cz.blahami2.utils.configuration.data.ConfigurationReader;
import cz.blahami2.utils.configuration.data.PropertiesReader;
import cz.blahami2.utils.configuration.data.readers.PropertiesConfigurationReader;
import cz.blahami2.utils.configuration.utils.ArgumentParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ConfigurationBuilderTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void configDefinedByPathInArguments() throws Exception {
        String propPath = getPropertiesPath();
        Properties properties = loadProperties( propPath );
        String[] args = new String[]{ "--config.path=" + propPath };
        ConfigurationBuilder builder = ConfigurationBuilder.newInstance()
                .setArguments( args, new ArgumentParser() )
                .setConfigPathArgumentName( "config.path", new PropertiesReader( Arrays.asList( new PropertiesConfigurationReader() ) ) );
        Configuration configuration = builder.build();
        for ( Map.Entry<Object, Object> entry : properties.entrySet() ) {
            assertThatConfigForKeyReturnsValue( configuration, entry.getKey(), entry.getValue() );
        }
    }

    @Test
    public void configDefinedByArgsAndDefault() throws Exception {
        String propPath = getPropertiesPath();
        String[] args = new String[]{ "--test1=something1", "--test2", "something2" };
        ConfigurationBuilder builder = ConfigurationBuilder.newInstance()
                .setDefaultProperties( propPath, new PropertiesReader( Arrays.asList( new PropertiesConfigurationReader() ) ) )
                .setArguments( args, new ArgumentParser() );
        Configuration configuration = builder.build();
        // from args - high priority
        assertThatConfigForKeyReturnsValue( configuration, "test1", "something1" );
        assertThatConfigForKeyReturnsValue( configuration, "test2", "something2" );
        // from default - low priority
        assertThatConfigForKeyReturnsValue( configuration, "test3", "test3" );
        assertThatConfigForKeyReturnsValue( configuration, "test4", "test4" );
    }

    private void assertThatConfigForKeyReturnsValue( Configuration configuration, Object key, Object expectedValue ) {
        assertThat( configuration.getValue( key.toString() ).get(), equalTo( expectedValue.toString() ) );
    }

    private Properties loadProperties( String path ) throws Exception {
        Properties properties = new Properties();
        properties.load( new FileInputStream( path ) );
        return properties;
    }

    private String getPropertiesPath() throws Exception {
        String resPath = new File( "src/test/resources" ).getAbsolutePath();
        String propPath = resPath + "/test.properties";
        return propPath;
    }
}