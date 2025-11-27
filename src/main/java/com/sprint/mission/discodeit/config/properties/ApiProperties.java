package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.api")
@Validated
public record ApiProperties(
    @NotBlank
    String serverUrl,
    @NotBlank
    String version
) {
}
