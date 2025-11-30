package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.cache")
@Validated
public record CacheProperties(
    @Positive(message = "discodeit.cache.maximum-size must be a positive number")
    @DefaultValue("1000")
    Integer maximumSize,
    @NotNull(message = "discodeit.cache.expire-after-access must not be null")
    Duration expireAfterAccess
) {
}
