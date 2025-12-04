package com.sprint.mission.discodeit.common.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("discodeit.storage")
@Validated
public record StorageProperties(
    @DefaultValue("local") StorageType type,
    @DefaultValue("1h") Duration orphanGrace,
    @DefaultValue Local local,
    @DefaultValue @Valid S3 s3
) {
    public record Local(
        @DefaultValue(".discodeit/storage") String rootPath
    ) {
    }

    public record S3(
        @NotBlank String accessKey,
        @NotBlank String secretKey,
        @NotBlank String region,
        @NotBlank String bucket,
        @DefaultValue("10m") Duration presignedUrlExpiration
    ) {
    }
}
