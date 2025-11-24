package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Validated
public record S3Properties(
    @NotBlank
    String accessKey,

    @NotBlank
    String secretKey,

    @NotBlank
    String region,

    @NotBlank
    String bucket,

    @NotNull
    Duration presignedUrlExpiration
) {
}
