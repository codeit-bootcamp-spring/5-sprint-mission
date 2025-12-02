package com.sprint.mission.discodeit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestKafkaConfig {

    @SuppressWarnings("unchecked")
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
