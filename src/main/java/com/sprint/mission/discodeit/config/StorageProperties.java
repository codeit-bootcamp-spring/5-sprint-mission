package com.sprint.mission.discodeit.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "discodeit.storage")
public record StorageProperties(@NotBlank String type, @Valid @NotNull Local local) {

    public record Local(@NotBlank String rootPath, Duration orphanGrace) {

    }
}
