package cz.blahami2.utils.configuration.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ArgumentParser {

    public Map<String, String> parse( String[] args ) {
        Map<String, String> map = new HashMap<>();
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i].startsWith( "--" ) ) {
                String key = args[i].substring( 2 );
                int indexOfEqual = key.indexOf( '=' );
                String value;
                if ( indexOfEqual >= 0 ) {
                    value = key.substring( indexOfEqual + 1 );
                    key = key.substring( 0, indexOfEqual );
                } else if ( i + 1 < args.length ) {
                    value = args[++i];
                } else {
                    break;
                }
                map.put( key, value );
            }
        }
        return map;
    }
}
