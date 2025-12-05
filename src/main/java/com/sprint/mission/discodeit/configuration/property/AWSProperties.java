package com.sprint.mission.discodeit.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
public record AWSProperties(
	String accessKey,
	String secretKey,
	String bucket,
	String region,
	long expiration
) {
}
