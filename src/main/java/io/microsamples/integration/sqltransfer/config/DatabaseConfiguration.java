package io.microsamples.integration.sqltransfer.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
public class DatabaseConfiguration {
    @Bean(name = "destinationDatasource")
    @Primary
    @ConfigurationProperties(prefix = "spring.destination.datasource")
    public DataSource destinationDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sourceDatasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource sourceDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DataSourceScriptDatabaseInitializer sourceScriptInitializer(DataSourceProperties properties){
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        final List<String> scriptLocations = scriptLocations(properties.getData(), "schema", "source");
        final List<String> dataLocations = scriptLocations(properties.getData(), "data", "source");

        return scriptDatabaseInitializer(sourceDatasource(), properties, settings, scriptLocations, dataLocations);
    }
    @Bean
    @Primary
    public DataSourceScriptDatabaseInitializer destinationScriptInitializer(DataSourceProperties properties){
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        final List<String> scriptLocations = scriptLocations(properties.getData(), "schema", "destination");
        final List<String> dataLocations = Collections.emptyList();

        return scriptDatabaseInitializer(destinationDatasource(), properties, settings, scriptLocations, dataLocations);
    }

    private DataSourceScriptDatabaseInitializer scriptDatabaseInitializer(
            DataSource dataSource
            , DataSourceProperties properties, DatabaseInitializationSettings settings, List<String> scriptLocations, List<String> dataLocations) {
        scriptLocations.addAll(dataLocations);
        settings.setDataLocations(scriptLocations);
        settings.setContinueOnError(properties.isContinueOnError());
        settings.setSeparator(properties.getSeparator());
        settings.setEncoding(properties.getSqlScriptEncoding());
        settings.setMode(mapMode(properties.getInitializationMode()));
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }

    private static List<String> scriptLocations(List<String> locations, String fallback, String platform) {
        if (locations != null) {
            return locations;
        }
        List<String> fallbackLocations = new ArrayList<>();
        fallbackLocations.add("optional:classpath*:" + fallback + "-" + platform + ".sql");
        fallbackLocations.add("optional:classpath*:" + fallback + ".sql");
        return fallbackLocations;
    }

    private static DatabaseInitializationMode mapMode(org.springframework.boot.jdbc.DataSourceInitializationMode mode) {
        switch (mode) {
            case ALWAYS:
                return DatabaseInitializationMode.ALWAYS;
            case EMBEDDED:
                return DatabaseInitializationMode.EMBEDDED;
            case NEVER:
                return DatabaseInitializationMode.NEVER;
            default:
                throw new IllegalStateException("Unexpected initialization mode '" + mode + "'");
        }
    }

}
