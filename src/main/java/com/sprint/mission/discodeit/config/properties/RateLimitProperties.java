package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("discodeit.rate-limit")
@Validated
public record RateLimitProperties(
    @Positive int maxAttempts,
    @Positive int windowSeconds,
    @Positive int blockSeconds
) {
}
