package com.sprint.mission.discodeit.storage.S3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage")
public class AWSProperties {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String region;
}
