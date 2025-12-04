package com.sprint.mission.discodeit.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.storage")
@Validated
public record StorageProperties(
    @DefaultValue("local") StorageType type,
    @DefaultValue("1h") Duration orphanGrace,
    @DefaultValue(".discodeit/storage") String localRootPath
) {
}
