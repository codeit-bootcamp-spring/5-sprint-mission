package com.sprint.mission.discodeit.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Getter
@Setter
public class AWSProperties {

	private String accessKey;
	private String secretKey;
	private String bucket;
	private String region;
	private long expiration = 600;
}
