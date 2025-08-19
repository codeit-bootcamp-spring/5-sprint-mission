package com.sprint.mission.discodeit.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "discodeit.repository")
public class RepositoryProps {

  private String fileDirectory = ".discodeit";
}
