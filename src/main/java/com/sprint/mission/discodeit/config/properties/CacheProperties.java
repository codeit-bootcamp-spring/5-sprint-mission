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
    @NotNull @Valid CacheSpec defaultSpec,
    @DefaultValue("{}") Map<String, @NotNull @Valid CacheSpec> caches
) {
    public record CacheSpec(
        @Positive @DefaultValue("1000") Integer maximumSize,
        Duration expireAfterAccess,
        Duration expireAfterWrite
    ) {
    }
}
