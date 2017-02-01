package cz.blahami2.utils.configuration;

import cz.blahami2.utils.configuration.data.PropertiesReader;
import cz.blahami2.utils.configuration.sources.MapConfigurationSource;
import cz.blahami2.utils.configuration.sources.PropertiesConfigurationSource;
import cz.blahami2.utils.configuration.utils.ArgumentParser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class ConfigurationBuilder {

    private String[] args;
    private ArgumentParser argumentParser;
    private boolean useEnvironmentVariables;
    private PropertiesReader propertiesReader;
    private List<String> configurationPaths = new ArrayList<>();
    private List<String> configPathArgNames = new ArrayList<>();
    private List<String> defaultConfigPaths = new ArrayList<>();

    private ConfigurationBuilder() {

    }

    // new instance
    public static ConfigurationBuilder newInstance( PropertiesReader propertiesReader ) {
        ConfigurationBuilder builder = new ConfigurationBuilder().setPropertiesReader( propertiesReader );
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
        ConfigurationBuilder builder = newInstance()
                .setArguments( args, argumentParser )
                .setUseEnvironmentVariables( true )
                .setPropertiesReader( propertiesReader );
        configArgNames.forEach( argName -> builder.addConfigPathArgumentName( argName ) );
        configPaths.forEach( path -> builder.addConfigurationPath( path ) );
        return builder;
    }

    public static ConfigurationBuilder newInstanceWithAll( String[] args, String configArgName, String configPath ) {
        ConfigurationBuilder builder = newInstance()
                .setArguments( args )
                .setUseEnvironmentVariables( true )
                .setConfigPathArgumentName( configArgName )
                .addConfigurationPath( configPath );
        return builder;
    }

    public static ConfigurationBuilder newInstanceWithAll( String[] args, Collection<String> configArgNames, Collection<String> configPaths ) {
        ConfigurationBuilder builder = newInstance()
                .setArguments( args )
                .setUseEnvironmentVariables( true );
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

    public ConfigurationBuilder setArguments( String[] args ) {
        return setArguments( args, createDefaultArgumentParser() );
    }

    // set load environment
    public ConfigurationBuilder setUseEnvironmentVariables( boolean useEnvironmentVariables ) {
        this.useEnvironmentVariables = useEnvironmentVariables;
        return this;
    }


    public ConfigurationBuilder setPropertiesReader( PropertiesReader propertiesReader ) {
        this.propertiesReader = propertiesReader;
        return this;
    }

    // set default properties
    public ConfigurationBuilder setDefaultProperties( String propertiesPath, PropertiesReader propertiesReader ) {
        return setPropertiesReader( propertiesReader ).addDefaultConfigurationPath( propertiesPath );
    }

    public ConfigurationBuilder setDefaultProperties( String propertiesPath ) {
        return setDefaultProperties( propertiesPath, createDefaultPropertiesReader() );
    }

    // set config arg name
    public ConfigurationBuilder setConfigPathArgumentName( String configPathArgumentName, PropertiesReader propertiesReader ) {
        ConfigurationBuilder builder = setPropertiesReader( propertiesReader );
        builder.addConfigPathArgumentName( configPathArgumentName );
        return builder;
    }

    public ConfigurationBuilder setConfigPathArgumentName( String configPathArgumentName ) {
        return setConfigPathArgumentName( configPathArgumentName, createDefaultPropertiesReader() );
    }

    // set config path
    public ConfigurationBuilder setConfigurationPath( String configurationPath, PropertiesReader propertiesReader ) {
        ConfigurationBuilder builder = setPropertiesReader( propertiesReader );
        builder.addConfigurationPath( configurationPath );
        return builder;
    }

    public ConfigurationBuilder setConfigurationPath( String configurationPath ) {
        return setConfigurationPath( configurationPath, createDefaultPropertiesReader() );
    }

    // add config path
    public ConfigurationBuilder addConfigurationPath( String configurationPath ) {
        this.configurationPaths.add( configurationPath );
        return this;
    }

    public ConfigurationBuilder addConfigPathArgumentName( String configPathArgumentName ) {
        this.configPathArgNames.add( configPathArgumentName );
        return this;
    }

    public ConfigurationBuilder addDefaultConfigurationPath( String defaultConfigurationPath ) {
        this.defaultConfigPaths.add( defaultConfigurationPath );
        return this;
    }

    public Configuration build() {
        List<ConfigurationSource> pathNameList = new ArrayList<>();
        LinkedList<ConfigurationSource> list = new LinkedList<>();
        // priorities
        // I. command line
        if ( args != null ) {
            if ( argumentParser == null ) {
                argumentParser = new ArgumentParser();
            }
            ConfigurationSource source = new MapConfigurationSource( argumentParser.parse( args ) );
            list.add( source );
            pathNameList.add( source );
        }
        // II. external config
        // by args and env
        if ( useEnvironmentVariables ) {
            pathNameList.add( new MapConfigurationSource( System.getenv() ) );
        }
        Set<String> configPathsSet = new HashSet<>();
        List<String> configPathsList = new ArrayList<>();
        configPathArgNames.stream()
                .forEachOrdered(
                        pathArgName -> pathNameList.stream()
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
        if ( useEnvironmentVariables ) {
            list.add( new MapConfigurationSource( System.getenv() ) );
        }
        // IV. default config
        addListByPath( list,
                defaultConfigPaths.stream()
                        .filter( path -> !configPathsSet.contains( path ) )
                        .peek( path -> configPathsSet.add( path ) )
                        .collect( Collectors.toList() )
        );
        return new Configuration( list );
    }

    private LinkedList<ConfigurationSource> addListByPath( LinkedList<ConfigurationSource> sourceList, List<String> pathList ) {
        pathList.stream()
                .map( path -> propertiesReader.read( path ) )
                .map( properties -> new PropertiesConfigurationSource( properties ) )
                .forEach( source -> sourceList.add( source ) );
        return sourceList;
    }

    private PropertiesReader createDefaultPropertiesReader() {
        return new PropertiesReader();
    }

    private ArgumentParser createDefaultArgumentParser() {
        return new ArgumentParser();
    }
}
