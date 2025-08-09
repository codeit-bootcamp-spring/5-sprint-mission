package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public AppStorageProperties appStorageProperties(AppProperties appProperties) {
        return appProperties.storage();
    }
}
