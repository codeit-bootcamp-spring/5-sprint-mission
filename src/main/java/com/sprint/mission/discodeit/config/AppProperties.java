package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        @NestedConfigurationProperty AppServerProperties server,
        @NestedConfigurationProperty AppMetadataProperties metadata,
        @NestedConfigurationProperty AppStorageProperties storage) {
}
