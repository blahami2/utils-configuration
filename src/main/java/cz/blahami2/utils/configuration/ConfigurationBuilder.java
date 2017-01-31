package cz.blahami2.utils.configuration;

import cz.blahami2.utils.configuration.data.PropertiesReader;
import cz.blahami2.utils.configuration.sources.MapConfigurationSource;
import cz.blahami2.utils.configuration.sources.PropertiesConfigurationSource;
import cz.blahami2.utils.configuration.utils.ArgumentParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ConfigurationBuilder {

    protected String[] args;
    protected ArgumentParser argumentParser;
    protected boolean useEnvironmentVariables;

    private ConfigurationBuilder() {

    }

    // new instance
    public static ConfigurationBuilderWithReader newInstance( PropertiesReader propertiesReader ) {
        ConfigurationBuilderWithReader builder = new ConfigurationBuilderWithReader( propertiesReader );
        return builder;
    }

    public static ConfigurationBuilder newInstance() {
        return new ConfigurationBuilder();
    }

    public static ConfigurationBuilder newInstanceWithAll( PropertiesReader propertiesReader, ArgumentParser argumentParser, String[] args, String configArgName, String configPath ) {
        ConfigurationBuilder builder = newInstance()
                .setArguments( args, argumentParser )
                .setUseEnvironmentVariables( true )
                .setConfigPathArgumentName( configArgName, propertiesReader )
                .addConfigurationPath( configPath );
        return builder;
    }

    public static ConfigurationBuilder newInstanceWithAll( PropertiesReader propertiesReader, ArgumentParser argumentParser, String[] args, Collection<String> configArgNames, Collection<String> configPaths ) {
        ConfigurationBuilderWithReader builder = newInstance()
                .setArguments( args, argumentParser )
                .setUseEnvironmentVariables( true )
                .setConfigurationReader( propertiesReader );
        configArgNames.forEach( argName -> builder.addConfigPathArgumentName( argName ) );
        configPaths.forEach( path -> builder.addConfigurationPath( path ) );
        return builder;
    }


    // set arguments
    public ConfigurationBuilder setArguments( String[] args, ArgumentParser argumentParser ) {
        this.args = args;
        this.argumentParser = argumentParser;
        return this;
    }

    // set load environment
    public ConfigurationBuilder setUseEnvironmentVariables( boolean useEnvironmentVariables ) {
        this.useEnvironmentVariables = useEnvironmentVariables;
        return this;
    }


    public ConfigurationBuilderWithReader setConfigurationReader( PropertiesReader propertiesReader ) {
        return new ConfigurationBuilderWithReader( this, propertiesReader );
    }

    // set default properties
    public ConfigurationBuilderWithReader setDefaultProperties( String propertiesPath, PropertiesReader propertiesReader ) {
        return setConfigurationReader( propertiesReader ).addDefaultConfigurationPath( propertiesPath );
    }

    // set config arg name
    public ConfigurationBuilderWithReader setConfigPathArgumentName( String configPathArgumentName, PropertiesReader propertiesReader ) {
        ConfigurationBuilderWithReader builder = setConfigurationReader( propertiesReader );
        builder.addConfigPathArgumentName( configPathArgumentName );
        return builder;
    }

    // set config path
    public ConfigurationBuilderWithReader setConfigurationPath( String configurationPath, PropertiesReader propertiesReader ) {
        ConfigurationBuilderWithReader builder = setConfigurationReader( propertiesReader );
        builder.addConfigurationPath( configurationPath );
        return builder;
    }

    public Configuration build() throws IOException {
        LinkedList<ConfigurationSource> list = new LinkedList<>();
        // priorities
        // I. command line
        list = addArgs( list );
        // II. external config - WITH READER ONLY
        // III. environment
        list = addEnv( list );
        // IV. default config - WITH READER ONLY
        return new Configuration( list );
    }

    protected LinkedList<ConfigurationSource> addArgs( LinkedList<ConfigurationSource> list ) {
        if ( args != null ) {
            if ( argumentParser == null ) {
                argumentParser = new ArgumentParser();
            }
            list.add( new MapConfigurationSource( argumentParser.parse( args ) ) );
        }
        return list;
    }

    protected LinkedList<ConfigurationSource> addEnv( LinkedList<ConfigurationSource> list ) {
        if ( useEnvironmentVariables ) {
            list.add( new MapConfigurationSource( System.getenv() ) );
        }
        return list;
    }

    public static class ConfigurationBuilderWithReader extends ConfigurationBuilder {
        private final PropertiesReader propertiesReader;
        private List<String> configurationPaths = new ArrayList<>();
        private List<String> configPathArgNames = new ArrayList<>();
        private List<String> defaultConfigPaths = new ArrayList<>();


        private ConfigurationBuilderWithReader( PropertiesReader propertiesReader ) {
            this.propertiesReader = propertiesReader;
        }

        private ConfigurationBuilderWithReader( ConfigurationBuilder configurationBuilder, PropertiesReader propertiesReader ) {
            this.propertiesReader = propertiesReader;
            // copy
            this.args = configurationBuilder.args;
            this.argumentParser = configurationBuilder.argumentParser;
            this.useEnvironmentVariables = configurationBuilder.useEnvironmentVariables;
            if ( configurationBuilder instanceof ConfigurationBuilderWithReader ) {
                configurationPaths.addAll( ( (ConfigurationBuilderWithReader) configurationBuilder ).configurationPaths );
                configPathArgNames.addAll( ( (ConfigurationBuilderWithReader) configurationBuilder ).configPathArgNames );
            }
        }

        // add config path
        public ConfigurationBuilderWithReader addConfigurationPath( String configurationPath ) {
            this.configurationPaths.add( configurationPath );
            return this;
        }

        public ConfigurationBuilderWithReader addConfigPathArgumentName( String configPathArgumentName ) {
            this.configPathArgNames.add( configPathArgumentName );
            return this;
        }

        public ConfigurationBuilderWithReader addDefaultConfigurationPath( String defaultConfigurationPath ) {
            this.defaultConfigPaths.add( defaultConfigurationPath );
            return this;
        }

        public Configuration build() throws IOException {
            LinkedList<ConfigurationSource> list = new LinkedList<>();
            // priorities
            // I. command line
            addArgs( list );
            // II. external config
            // by args
            Set<String> configPathsSet = new HashSet<>();
            List<String> configPathsList = new ArrayList<>();
            configPathArgNames.stream()
                    .forEachOrdered(
                            pathArgName -> list.stream()
                                    .map( source -> source.getValue( pathArgName ) )
                                    .forEach(
                                            configPath -> configPath.filter( path -> !configPathsSet.contains( path ) )
                                                    .ifPresent( path -> {
                                                        configPathsList.add( path );
                                                        configPathsSet.add( path );
                                                    } )
                                    )
                    );
            // by pathname
            configurationPaths.stream()
                    .filter( path -> !configPathsSet.contains( path ) )
                    .forEachOrdered( path -> {
                        configPathsList.add( path );
                        configPathsSet.add( path );
                    } );
            // create sources
            addListByPath( list, configPathsList );
            // III. environment
            addEnv( list );
            // IV. default config
            addListByPath( list,
                    defaultConfigPaths.stream()
                            .filter( path -> !configPathsSet.contains( path ) )
                            .peek( path -> configPathsSet.add( path ) )
                            .collect( Collectors.toList() )
            );
            return new Configuration( list );
        }

        private LinkedList<ConfigurationSource> addListByPath( LinkedList<ConfigurationSource> sourceList, List<String> pathList ) throws IOException {
            for ( String configPath : pathList ) {
                Properties p = propertiesReader.read( configPath );
                ConfigurationSource configurationSource = new PropertiesConfigurationSource( p );
                sourceList.add( configurationSource );
            }
            return sourceList;
        }
    }
}
