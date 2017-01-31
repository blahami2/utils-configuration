package cz.blahami2.utils.configuration.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface ConfigurationReader {
    String getExtension();

    Properties loadProperties( File file ) throws IOException;
}
