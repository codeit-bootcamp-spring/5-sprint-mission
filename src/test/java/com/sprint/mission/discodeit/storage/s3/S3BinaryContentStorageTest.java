package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;

    @BeforeEach
    void setup() throws IOException {
        java.util.Properties properties = new java.util.Properties();
        properties.load(new java.io.FileInputStream(".env"));

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String region = properties.getProperty("AWS_S3_REGION");
        String bucket = properties.getProperty("AWS_S3_BUCKET");

        storage = new S3BinaryContentStorage(
                accessKey,
                secretKey,
                region,
                bucket,
                600
        );
    }

    @Test
    void put_success() {
        //given
        UUID id = UUID.randomUUID();
        byte[] data = "Test S3 Storage Content".getBytes();

        //when
        UUID result = storage.put(id, data);

        //then
        assertThat(result).isEqualTo(id);
    }

    @Test
    void get_success() throws IOException {
        //given
        UUID id = UUID.randomUUID();
        byte[] originalData = "Test S3 Storage Get".getBytes();
        storage.put(id, originalData);

        //when
        InputStream inputStream = storage.get(id);
        byte[] retrievedData = inputStream.readAllBytes();

        //then
        assertThat(retrievedData).isEqualTo(originalData);
        assertThat(new String(retrievedData)).isEqualTo("Test S3 Storage Get");
    }

    @Test
    void download_PresignedUrl_redirect_success() {
        //given
        UUID id = UUID.randomUUID();
        byte[] data = "Test S3 Storage Download".getBytes();
        storage.put(id, data);

        BinaryContentDTO dto = BinaryContentDTO.builder()
                .id(id)
                .contentType("text/plain")
                .build();

        //when
        ResponseEntity<?> response = storage.download(dto);

        //then
        assertThat(response.getStatusCodeValue()).isEqualTo(302);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String presignedUrl = response.getHeaders().getLocation().toString();
        assertThat(presignedUrl).contains("discodeit-binary-content-storage");
        assertThat(presignedUrl).contains(id.toString());
        assertThat(presignedUrl).contains("X-Amz-Algorithm");
    }

    @Test
    void put_get_download_integrationTest() throws IOException {
        //given
        UUID id = UUID.randomUUID();
        byte[] originalData = "Integration Test Data".getBytes();

        //when
        //then
        UUID uploadedId = storage.put(id, originalData);
        assertThat(uploadedId).isEqualTo(id);

        InputStream inputStream = storage.get(id);
        byte[] retrievedData = inputStream.readAllBytes();
        assertThat(retrievedData).isEqualTo(originalData);

        BinaryContentDTO dto = BinaryContentDTO.builder()
                .id(id)
                .contentType("text/plain")
                .build();
        ResponseEntity<?> response = storage.download(dto);
        assertThat(response.getStatusCodeValue()).isEqualTo(302);
    }
}