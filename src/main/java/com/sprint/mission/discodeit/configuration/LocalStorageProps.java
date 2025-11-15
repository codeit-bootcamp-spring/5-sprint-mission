package com.sprint.mission.discodeit.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "discodeit.storage.local")
public class LocalStorageProps {

	@Getter
	@Setter
	private String rootPath = ".discodeit";
}
