package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "discodeit.storage")
@Validated
public record StorageProperties(
    @NotBlank
    String type,

    @DefaultValue("1h")
    Duration orphanGrace,

    Local local
) {

    public record Local(String rootPath) {

        public Local {
            if (rootPath == null || rootPath.isBlank()) {
                rootPath = ".discodeit/storage";
            }
        }
    }
}
