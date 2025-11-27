package com.sprint.mission.discodeit.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
public record S3Properties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    @DefaultValue("10m")
    Duration presignedUrlExpiration
) {
    public boolean isConfigured() {
        return hasText(accessKey)
            && hasText(secretKey)
            && hasText(region)
            && hasText(bucket);
    }
}
