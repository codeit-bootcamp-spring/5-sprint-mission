package com.sprint.mission.discodeit.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.local")
public record LocalStorageProperties(
	String rootPath
) {
}
