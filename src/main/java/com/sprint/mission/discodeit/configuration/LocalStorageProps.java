package com.sprint.mission.discodeit.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.local")
public class LocalStorageProps {

  @Getter
  @Setter
  private String rootPath = ".discodeit";
}
