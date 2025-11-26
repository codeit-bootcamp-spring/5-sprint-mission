package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "discodeit.storage")
@Validated
public record StorageProperties(
    @NotBlank
    String type,

    @NotNull
    @Valid
    Local local
) {

    public record Local(
        @NotBlank
        String rootPath,

        Duration orphanGrace
    ) {

        public Local {
            if (orphanGrace == null) {
                orphanGrace = Duration.ofHours(1);
            }
        }
    }
}
