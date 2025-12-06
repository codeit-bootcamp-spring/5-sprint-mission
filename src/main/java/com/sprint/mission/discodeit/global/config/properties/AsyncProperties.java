package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("discodeit.async")
@Validated
public record AsyncProperties(
    @DefaultValue("10") @Min(1) int corePoolSize,
    @DefaultValue("20") @Min(1) int maxPoolSize,
    @DefaultValue("500") @Min(0) int queueCapacity,
    @DefaultValue("30") @Min(0) int awaitTerminationSeconds
) {
}
