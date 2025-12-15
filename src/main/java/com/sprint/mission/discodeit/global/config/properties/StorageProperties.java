package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.storage")
@Validated
public record StorageProperties(
    @DefaultValue("1h") Duration orphanGrace,
    @DefaultValue("3600000") @Positive long cleanupInterval,
    @DefaultValue @Valid Retry retry
) {
    public record Retry(
        @DefaultValue("3") @Min(1) int maxAttempts,
        @DefaultValue("2000") @Positive long backoffDelay,
        @DefaultValue("3") @Positive double backoffMultiplier
    ) {
    }
}
