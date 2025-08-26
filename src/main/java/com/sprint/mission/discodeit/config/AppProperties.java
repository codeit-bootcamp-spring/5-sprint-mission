package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
    @NestedConfigurationProperty AppStorageProperties storage) {

}
