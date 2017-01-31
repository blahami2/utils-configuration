package cz.blahami2.utils.configuration.sources;

import cz.blahami2.utils.configuration.ConfigurationSource;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class ConfigurationSourceImplTest {

    private ConfigurationSource config;

    abstract public ConfigurationSource getConfig( Map<String, String> data );

    @Before
    public void setUp() throws Exception {
        this.config = getConfig( getData() );
    }

    private Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put( "bool", "true" );
        map.put( "int", "123" );
        map.put( "double", "1.25" );
        map.put( "string", "text" );
        return map;
    }

    @Test
    public void boolReturnsTrue() throws Exception {
        assertThat( config.getValue( "bool", Boolean.class ).get(), equalTo( true ) );
    }

    @Test
    public void intReturns123() throws Exception {
        assertThat( config.getValue( "int", Integer.class ).get(), equalTo( 123 ) );
    }

    @Test
    public void doubleReturns123() throws Exception {
        assertThat( config.getValue( "double", Double.class ).get(), equalTo( 1.25 ) );
    }

    @Test
    public void stringReturnsText() throws Exception {
        assertThat( config.getValue( "string" ).get(), equalTo( "text" ) );
    }

    @Test
    public void somethingReturnsEmptyOptional() throws Exception {
        assertThat( config.getValue( "something" ).isPresent(), equalTo( false ) );
    }

    @Test
    public void boolReturnsTrueWithMapper() throws Exception {
        assertThat( config.getValue( "bool", b -> Boolean.parseBoolean( b ) ).get(), equalTo( true ) );
    }
}
