package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;


/**
 * Lookup environment variables defined through Windows Azure which are prefixed with 'APPSETTINGS_'
 */
@Configuration
public class AzurePropertySourceConfig
{
    private static final String AZURE_PREFIX = "APPSETTINGS_";

    @Autowired
    private ConfigurableEnvironment env;


    @Bean
    @Lazy(false)
    public PropertySource azurePropertySource()
    {
        final PropertySource azurePropSource = new PropertySource("AzureProperties") {
            @Override
            public Object getProperty(final String name) {
                if (name != null && !name.startsWith(AZURE_PREFIX)) {
                    return env.getProperty( AZURE_PREFIX + name );
                } else {
                    return null;
                }
            }
        };
        final MutablePropertySources sources = env.getPropertySources();
        sources.addFirst(azurePropSource);
        return azurePropSource;
    }

}
