package com.sprint.mission.discodeit.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "discodeit.api")
public record ApiProperties(
    @NotNull
    String serverUrl
) {
}
