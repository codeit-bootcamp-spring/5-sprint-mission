package com.sprint.mission.discodeit.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties("discodeit.storage")
@Validated
public record StorageProperties(
    @DefaultValue("LOCAL") StorageType type,
    @DefaultValue("1h") Duration orphanGrace,
    Local local
) {
    public record Local(String rootPath) {
        public Local {
            if (!hasText(rootPath)) {
                rootPath = ".discodeit/storage";
            }
        }
    }
}
