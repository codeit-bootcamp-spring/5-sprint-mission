package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.metadata")
public record AppMetadataProperties(String region, String owner) {
}
