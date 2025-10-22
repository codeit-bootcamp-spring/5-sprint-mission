package com.sprint.mission.discodeit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:properties/env.properties", ignoreResourceNotFound = true)
})
public class PropertyConfig {

}
