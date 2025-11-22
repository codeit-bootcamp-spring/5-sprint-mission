package com.sprint.mission.discodeit.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.error")
public record ErrorHandlingProperties(boolean exposeDetails) {
}
