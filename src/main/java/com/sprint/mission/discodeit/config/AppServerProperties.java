package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.server")
public record AppServerProperties(int port, String url, int timeout) {
}
