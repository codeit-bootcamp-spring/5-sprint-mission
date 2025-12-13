package com.sprint.mission.discodeit.support;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    private static final String TEST_BUCKET = "test-bucket";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection(name = "redis")
    @SuppressWarnings("resource")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);

    @Container
    @ServiceConnection
    @SuppressWarnings("deprecation")
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
        .withServices(S3);

    @BeforeAll
    static void createS3Bucket() {
        try (S3Client s3Client = S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .forcePathStyle(true)
            .build()) {

            s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(TEST_BUCKET)
                .build());
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Kafka 설정 - Testcontainers bootstrap servers로 오버라이드
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // LocalStack S3 설정
        registry.add("discodeit.storage.type", () -> "s3");
        registry.add("discodeit.s3.access-key", () -> localstack.getAccessKey());
        registry.add("discodeit.s3.secret-key", () -> localstack.getSecretKey());
        registry.add("discodeit.s3.region", () -> localstack.getRegion());
        registry.add("discodeit.s3.bucket", () -> TEST_BUCKET);
        registry.add("discodeit.s3.endpoint", () -> localstack.getEndpointOverride(S3).toString());
    }
}
