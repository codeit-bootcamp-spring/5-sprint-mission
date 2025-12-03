package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing
@EnableScheduling
@PropertySource(value = "classpath:.env.properties", ignoreResourceNotFound = true)
@EnableConfigurationProperties(JwtProperties.class)
public class AppConfig {
}