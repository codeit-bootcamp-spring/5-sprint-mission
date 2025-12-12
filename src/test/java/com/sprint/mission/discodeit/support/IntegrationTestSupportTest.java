package com.sprint.mission.discodeit.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTestSupportTest extends IntegrationTestSupport {

    @Autowired
    DataSource dataSource;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    S3Client s3Client;

    @Test
    void all() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(redis.isRunning()).isTrue();
        assertThat(kafka.isRunning()).isTrue();
        assertThat(localstack.isRunning()).isTrue();

        assertThat(dataSource).isNotNull();
        assertThat(redisTemplate).isNotNull();
        assertThat(kafkaTemplate).isNotNull();
        assertThat(s3Client).isNotNull();
    }
}
