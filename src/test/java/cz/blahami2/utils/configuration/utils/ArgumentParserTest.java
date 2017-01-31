package cz.blahami2.utils.configuration.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ArgumentParserTest {
    private ArgumentParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ArgumentParser();
    }

    @Test
    public void parseEmptyStringReturnsEmptyMap() throws Exception {
        String[] args = new String[]{};
        assertThat( parser.parse( args ), equalTo( Collections.emptyMap() ) );
    }

    @Test
    public void parseMessReturnsEmptyMap() throws Exception {
        String[] args = new String[]{ "s", "o", "m", "e", "t", "h", "i", "n", "g" };
        assertThat( parser.parse( args ), equalTo( Collections.emptyMap() ) );
    }

    @Test
    public void parseArgsEqualsReturnsArgs() throws Exception {
        String[] args = new String[]{ "--data=stuff", "--a" };
        assertThat( parser.parse( args ), equalTo( new HashMap<String, String>() {{
            put( "data", "stuff" );
        }} ) );
    }

    @Test
    public void parseArgsSeparatedReturnsArgs() throws Exception {
        String[] args = new String[]{ "--data", "stuff", "--a" };
        assertThat( parser.parse( args ), equalTo( new HashMap<String, String>() {{
            put( "data", "stuff" );
        }} ) );
    }
}