package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        AppServerProperties server,
        List<String> admins,
        AppMetadataProperties metadata,
        AppStorageProperties storage) {
}
