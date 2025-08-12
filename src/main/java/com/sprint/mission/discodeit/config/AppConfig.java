package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public AppServerProperties appServerProperties(AppProperties appProperties) {
        return appProperties.server();
    }

    @Bean
    public AppMetadataProperties appMetadataProperties(AppProperties appProperties) {
        return appProperties.metadata();
    }

    @Bean
    public AppStorageProperties appStorageProperties(AppProperties appProperties) {
        return appProperties.storage();
    }
}
