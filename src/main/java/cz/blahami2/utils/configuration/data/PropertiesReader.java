package cz.blahami2.utils.configuration.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class PropertiesReader {
    private Map<String, ConfigurationReader> formatReaderMap = new HashMap<>();

    public PropertiesReader( Collection<ConfigurationReader> readers ) {
        this.formatReaderMap = readers.stream().collect( Collectors.toMap( reader -> reader.getExtension(), reader -> reader ) );
    }

    public Properties read( String path ) throws IOException {
        File file = new File( path );
        if ( !file.exists() ) {
            throw new FileNotFoundException( "File does not exist: " + path );
        }
        String extension = path.substring( path.lastIndexOf( '.' ) + 1 );
        if(!formatReaderMap.containsKey( extension )){
            throw new IllegalArgumentException( "Unknown extension: " + extension );
        }
        return formatReaderMap.get( extension ).loadProperties(file);
    }
}
