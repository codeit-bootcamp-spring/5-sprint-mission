package com.sprint.mission.discodeit.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("discodeit.storage.s3")
public record S3Properties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    @DefaultValue("10m") Duration presignedUrlExpiration
) {
}
