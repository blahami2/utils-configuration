package cz.blahami2.utils.configuration;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ConfigurationSourceTest {

    @Test
    public void mapperCanMapBoolean() throws Exception {
        Optional<Boolean> aTrue = ConfigurationSource.CommonMappers.map( Boolean.class, "true" );
        assertThat( aTrue.isPresent(), equalTo( true ) );
        assertThat( aTrue.get(), equalTo( true ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void mapperCannotMapObject() throws Exception {
        Optional<Object> o = ConfigurationSource.CommonMappers.map( Object.class, new Object() );
    }

    @Test
    public void mapperCanMapCharacter() throws Exception {
        Optional<Character> ch = ConfigurationSource.CommonMappers.map( Character.class, "c" );
        assertThat( ch.get(), equalTo( 'c' ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void mapperCannotMapEmptyString() throws Exception {
        ConfigurationSource.CommonMappers.map( Character.class, "ca" );
    }

    @Test
    public void mapperCanMapShort() throws Exception {
        Optional<Short> aShort = ConfigurationSource.CommonMappers.map( Short.class, "128" );
        assertThat( aShort.get(), equalTo( (short) 128 ) );
    }

    @Test
    public void mapperCanMapInteger() throws Exception {
        Optional<Integer> aInt = ConfigurationSource.CommonMappers.map( Integer.class, "2154154" );
        assertThat( aInt.get(), equalTo( 2154154 ) );
    }

    @Test
    public void mapperCanMapDouble() throws Exception {
        Optional<Double> aDouble = ConfigurationSource.CommonMappers.map( Double.class, "2.154154" );
        assertThat( aDouble.get(), equalTo( 2.154154 ) );
    }
}