package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.rate-limit")
@Validated
public record RateLimitProperties(
    @Positive int maxAttempts,
    @DefaultValue("1m") Duration windowDuration,
    @DefaultValue("5m") Duration blockDuration
) {
}
