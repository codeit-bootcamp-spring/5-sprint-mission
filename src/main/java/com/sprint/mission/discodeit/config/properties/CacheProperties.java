package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties("discodeit.cache")
@Validated
public record CacheProperties(
    @NotNull(message = "discodeit.cache.default-spec must not be null")
    @Valid
    DefaultSpec defaultSpec,
    Map<String, @Valid CacheSpec> caches
) {

    public record DefaultSpec(
        @Positive(message = "discodeit.cache.default-spec.maximum-size must be positive")
        @DefaultValue("1000")
        Integer maximumSize,
        @NotNull(message = "discodeit.cache.default-spec.expire-after-access must not be null")
        Duration expireAfterAccess
    ) {
    }

    public record CacheSpec(
        @Positive(message = "discodeit.cache.maximum-size must be positive")
        Integer maximumSize,
        Duration expireAfterAccess,
        Duration expireAfterWrite
    ) {
    }
}
