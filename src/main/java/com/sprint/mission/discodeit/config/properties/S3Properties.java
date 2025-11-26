package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;
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

    @DefaultValue("10m")
    Duration presignedUrlExpiration
) {

    @Override
    @NonNull
    public String toString() {
        return "S3Properties{"
            + "region='" + region + '\''
            + ", bucket='" + bucket + '\''
            + ", presignedUrlExpiration=" + presignedUrlExpiration
            + '}';
    }
}
