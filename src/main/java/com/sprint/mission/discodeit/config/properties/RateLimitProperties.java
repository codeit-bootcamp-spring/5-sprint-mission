package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.rate-limit")
@Validated
public record RateLimitProperties(
    @Positive(message = "discodeit.rate-limit.max-attempts must be positive")
    int maxAttempts,
    @Positive(message = "discodeit.rate-limit.window-seconds must be positive")
    int windowSeconds,
    @Positive(message = "discodeit.rate-limit.block-seconds must be positive")
    int blockSeconds
) {
}
