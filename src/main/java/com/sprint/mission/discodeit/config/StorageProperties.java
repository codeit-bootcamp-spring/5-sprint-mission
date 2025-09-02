package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage")
public record StorageProperties(String type, Local local) {

    public record Local(String rootPath) {

    }
}
