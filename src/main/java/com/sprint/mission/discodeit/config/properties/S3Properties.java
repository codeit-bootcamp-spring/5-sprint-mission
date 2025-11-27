package com.sprint.mission.discodeit.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
public record S3Properties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    Duration presignedUrlExpiration
) {
    public S3Properties {
        if (presignedUrlExpiration == null) {
            presignedUrlExpiration = Duration.ofMinutes(10);
        }
    }

    public boolean isConfigured() {
        return hasText(accessKey)
            && hasText(secretKey)
            && hasText(region)
            && hasText(bucket);
    }
}
