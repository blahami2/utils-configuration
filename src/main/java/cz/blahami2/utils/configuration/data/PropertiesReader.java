package cz.blahami2.utils.configuration.data;

import cz.blahami2.utils.configuration.data.readers.PropertiesConfigurationReader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class PropertiesReader {
    private Map<String, ConfigurationReader> formatReaderMap = new HashMap<>();

    public PropertiesReader() {
        this.formatReaderMap = Stream.of( new PropertiesConfigurationReader() ).collect( Collectors.toMap( reader -> reader.getExtension(), reader -> reader ) );
    }

    public PropertiesReader( Collection<ConfigurationReader> readers ) {
        this.formatReaderMap = readers.stream().collect( Collectors.toMap( reader -> reader.getExtension(), reader -> reader ) );
    }


    public Properties read( String path ) {
        File file = new File( path );
        if ( !file.exists() ) {
            throw new RuntimeException( "File does not exist: " + path );
        }
        String extension = path.substring( path.lastIndexOf( '.' ) + 1 );
        if ( !formatReaderMap.containsKey( extension ) ) {
            throw new IllegalArgumentException( "Unknown extension: " + extension );
        }
        try {
            return formatReaderMap.get( extension ).loadProperties( file );
        } catch ( IOException e ) {
            throw new RuntimeException( "Exception thrown when loading properties from a file.", e );
        }
    }
}
