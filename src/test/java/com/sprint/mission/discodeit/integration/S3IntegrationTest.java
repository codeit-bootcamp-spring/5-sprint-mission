package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.UUID;

import static com.sprint.mission.discodeit.support.StorageTestFixtures.TEST_CONTENT;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createBinaryContentDto;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createTestContent;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EnabledIf(value = "#{environment['discodeit.storage.type'] == 's3'}", loadContext = true)
@DisplayName("AWS S3 통합 테스트")
class S3IntegrationTest extends IntegrationTest {

    private final UUID testId = UUID.randomUUID();
    private final byte[] testData = createTestContent(TEST_CONTENT);

    @Autowired
    private S3BinaryContentStorage s3BinaryContentStorage;

    @Value("${discodeit.storage.s3.bucket}")
    private String bucket;

    @Autowired
    private S3Client s3Client;

    @AfterEach
    void tearDown() {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(testId.toString())
                .build();

            s3Client.deleteObject(deleteRequest);
        } catch (NoSuchKeyException e) {
            // 객체가 이미 없는 경우는 무시
        }
    }

    @Test
    @DisplayName("S3에 파일 업로드 성공")
    void putSuccess() {
        // when
        UUID resultId = s3BinaryContentStorage.put(testId, testData);

        // then
        assertThat(resultId).isEqualTo(testId);
    }

    @Test
    @DisplayName("Presigned URL 생성 성공")
    void downloadSuccess() {
        // given
        s3BinaryContentStorage.put(testId, testData);
        BinaryContentDto dto = createBinaryContentDto(
            testId, "test.txt", testData.length, "text/plain"
        );

        // when
        ResponseEntity<Void> response = s3BinaryContentStorage.download(dto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String location = response.getHeaders().getLocation().toString();
        assertThat(location).contains(bucket);
        assertThat(location).contains(testId.toString());
    }
}
