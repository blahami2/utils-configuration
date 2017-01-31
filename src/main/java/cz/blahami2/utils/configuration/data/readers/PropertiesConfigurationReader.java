package cz.blahami2.utils.configuration.data.readers;

import cz.blahami2.utils.configuration.data.ConfigurationReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class PropertiesConfigurationReader implements ConfigurationReader {
    @Override
    public String getExtension() {
        return "properties";
    }

    @Override
    public Properties loadProperties( File file ) throws IOException {
        Properties properties = new Properties();
        properties.load( new FileInputStream( file ) );
        return properties;
    }
}
