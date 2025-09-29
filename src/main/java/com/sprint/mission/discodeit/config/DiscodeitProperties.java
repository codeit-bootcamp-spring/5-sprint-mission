package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit")
public record DiscodeitProperties(Storage storage) {

    public record Storage(StorageType type, Local local) {}

    public record Local(String rootPath) {}

    public enum StorageType {
        LOCAL
    }
}
