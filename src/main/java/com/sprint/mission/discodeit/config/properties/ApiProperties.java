package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.api")
@Validated
public record ApiProperties(
    @NotNull
    String serverUrl
) {
}
