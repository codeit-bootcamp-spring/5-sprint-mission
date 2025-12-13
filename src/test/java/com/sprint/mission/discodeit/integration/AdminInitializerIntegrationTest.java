package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@DisplayName("AdminInitializer 통합 테스트")
class AdminInitializerIntegrationTest {

    private static final String ADMIN_USERNAME = "testadmin";
    private static final String ADMIN_EMAIL = "testadmin@example.com";
    private static final String ADMIN_PASSWORD = "AdminP@ss123!";

    @Nested
    @DisplayName("admin.enabled=true일 때")
    @SpringBootTest
    @Testcontainers
    @ActiveProfiles("test")
    class WhenAdminEnabled {

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

        @Container
        @SuppressWarnings("resource")
        static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

        @Container
        @SuppressWarnings("deprecation")
        static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

        @Container
        static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3"))
            .withServices(S3);

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
            // Database
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);

            // Redis
            registry.add("spring.data.redis.host", redis::getHost);
            registry.add("spring.data.redis.port", redis::getFirstMappedPort);

            // Kafka
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

            // S3
            registry.add("discodeit.storage.type", () -> "s3");
            registry.add("discodeit.s3.access-key", localstack::getAccessKey);
            registry.add("discodeit.s3.secret-key", localstack::getSecretKey);
            registry.add("discodeit.s3.region", localstack::getRegion);
            registry.add("discodeit.s3.bucket", () -> "test-bucket");
            registry.add("discodeit.s3.endpoint",
                () -> localstack.getEndpointOverride(S3).toString());

            // Admin 설정
            registry.add("discodeit.admin.enabled", () -> "true");
            registry.add("discodeit.admin.username", () -> ADMIN_USERNAME);
            registry.add("discodeit.admin.email", () -> ADMIN_EMAIL);
            registry.add("discodeit.admin.password", () -> ADMIN_PASSWORD);
        }

        @Autowired
        private UserRepository userRepository;

        @Test
        @DisplayName("어드민 계정이 자동으로 생성된다")
        void adminAccountIsCreated() {
            // when
            Optional<User> admin = userRepository.findByUsername(ADMIN_USERNAME);

            // then
            assertThat(admin).isPresent();
            assertThat(admin.get().getEmail()).isEqualTo(ADMIN_EMAIL);
            assertThat(admin.get().getRole()).isEqualTo(Role.ADMIN);
        }
    }

    @Nested
    @DisplayName("admin.enabled=false일 때")
    @SpringBootTest
    @Testcontainers
    @ActiveProfiles("test")
    class WhenAdminDisabled {

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

        @Container
        @SuppressWarnings("resource")
        static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

        @Container
        @SuppressWarnings("deprecation")
        static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

        @Container
        static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3"))
            .withServices(S3);

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
            // Database
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);

            // Redis
            registry.add("spring.data.redis.host", redis::getHost);
            registry.add("spring.data.redis.port", redis::getFirstMappedPort);

            // Kafka
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

            // S3
            registry.add("discodeit.storage.type", () -> "s3");
            registry.add("discodeit.s3.access-key", localstack::getAccessKey);
            registry.add("discodeit.s3.secret-key", localstack::getSecretKey);
            registry.add("discodeit.s3.region", localstack::getRegion);
            registry.add("discodeit.s3.bucket", () -> "test-bucket");
            registry.add("discodeit.s3.endpoint",
                () -> localstack.getEndpointOverride(S3).toString());

            // Admin 비활성화 (기본값)
            registry.add("discodeit.admin.enabled", () -> "false");
        }

        @Autowired
        private UserRepository userRepository;

        @Test
        @DisplayName("어드민 계정이 생성되지 않는다")
        void adminAccountIsNotCreated() {
            // when
            Optional<User> admin = userRepository.findByUsername(ADMIN_USERNAME);

            // then
            assertThat(admin).isEmpty();
        }
    }
}
