package com.sprint.mission.discodeit.infrastructure.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.storage")
@Validated
public record StorageProperties(
    @DefaultValue("1h") Duration orphanGrace,
    @DefaultValue("3600000") long cleanupInterval
) {
}
