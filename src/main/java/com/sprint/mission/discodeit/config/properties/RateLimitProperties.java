package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.rate-limit")
@Validated
public record RateLimitProperties(
    @Positive(message = "Max attempts must be positive")
    int maxAttempts,

    @Positive(message = "Window seconds must be positive")
    int windowSeconds,

    @Positive(message = "Block seconds must be positive")
    int blockSeconds
) {

}
