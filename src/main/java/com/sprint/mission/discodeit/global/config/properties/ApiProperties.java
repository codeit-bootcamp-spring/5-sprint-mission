package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("discodeit.api")
@Validated
public record ApiProperties(
    @DefaultValue("") String serverUrl,
    @NotBlank String version
) {
}
