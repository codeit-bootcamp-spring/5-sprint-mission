package com.sprint.mission.discodeit.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("discodeit.s3")
public record S3Properties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    String endpoint,
    @DefaultValue("10m") Duration presignedUrlExpiration
) {
}
