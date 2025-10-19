// src/test/java/com/sprint/mission/discodeit/storage/s3/S3BinaryContentStorageTest.java
package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.S3Config.S3Props;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner presigner;

    // ✅ Record는 final 메서드라 mock 불가 → 실제 인스턴스 사용
    private S3Props props;

    private S3BinaryContentStorage storage;

    private static final String TEST_BUCKET = "test-bucket";
    private static final long EXPIRATION_SECONDS = 3600L;

    @BeforeEach
    void setUp() {
        // ✅ 실제 record 인스턴스 생성
        props = new S3Props(TEST_BUCKET, EXPIRATION_SECONDS);
        storage = new S3BinaryContentStorage(s3Client, presigner, props);
    }

    @Test
    void put_success() {
        // Given
        UUID id = UUID.randomUUID();
        byte[] data = "test content".getBytes();

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).build());

        // When
        UUID result = storage.put(id, data);

        // Then
        assertThat(result).isEqualTo(id);

        ArgumentCaptor<PutObjectRequest> reqCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(reqCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest capturedReq = reqCaptor.getValue();
        assertThat(capturedReq.bucket()).isEqualTo(TEST_BUCKET);
        assertThat(capturedReq.key()).isEqualTo(id.toString());
        assertThat(capturedReq.contentType()).isEqualTo("application/octet-stream");
    }

    @Test
    void put_throwsException_whenFileAlreadyExists() {
        // Given
        UUID id = UUID.randomUUID();
        byte[] data = "test content".getBytes();

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        // When & Then
        assertThatThrownBy(() -> storage.put(id, data))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void get_success() {
        // Given
        UUID id = UUID.randomUUID();
        byte[] expectedData = "file content".getBytes();

        // ✅ ResponseInputStream 올바르게 모킹
        GetObjectResponse response = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> responseStream =
                new ResponseInputStream<>(response,
                        AbortableInputStream.create(new ByteArrayInputStream(expectedData)));

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(responseStream);

        // When
        InputStream result = storage.get(id);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());

        GetObjectRequest capturedReq = captor.getValue();
        assertThat(capturedReq.bucket()).isEqualTo(TEST_BUCKET);
        assertThat(capturedReq.key()).isEqualTo(id.toString());
    }

    @Test
    void get_throwsException_whenFileDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).build());

        // When & Then
        assertThatThrownBy(() -> storage.get(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("does not exist");

        verify(s3Client, never()).getObject(any(GetObjectRequest.class));
    }

    @Test
    void download_success_withContentType() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String contentType = "image/png";
        BinaryContentDto metadata = new BinaryContentDto(id, "test.png", 1024L, contentType);
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/" + id + "?presigned=true";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // When
        ResponseEntity<?> response = storage.download(metadata);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(expectedUrl);

        verify(presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void download_success_withoutContentType() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        BinaryContentDto metadata = new BinaryContentDto(id, "test.bin", 2048L, null);
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/" + id + "?presigned=true";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // When
        ResponseEntity<?> response = storage.download(metadata);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(expectedUrl);
    }

    @Test
    void download_returns404_whenFileDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        BinaryContentDto metadata = new BinaryContentDto(id, "test.png", 1024L, "image/png");

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).build());

        // When
        ResponseEntity<?> response = storage.download(metadata);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).asString().contains("Not found");

        verify(presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void generatePresignedUrl_withContentType() throws Exception {
        // Given
        String key = "test-key";
        String contentType = "application/pdf";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-key?presigned=true";

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // When
        String result = storage.generatePresignedUrl(key, contentType);

        // Then
        assertThat(result).isEqualTo(expectedUrl);

        ArgumentCaptor<GetObjectPresignRequest> captor =
                ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(presigner).presignGetObject(captor.capture());

        GetObjectPresignRequest capturedReq = captor.getValue();
        assertThat(capturedReq.signatureDuration().getSeconds()).isEqualTo(EXPIRATION_SECONDS);
    }

    @Test
    void generatePresignedUrl_withoutContentType() throws Exception {
        // Given
        String key = "test-key";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-key?presigned=true";

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // When
        String result = storage.generatePresignedUrl(key, null);

        // Then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void generatePresignedUrl_withEmptyContentType() throws Exception {
        // Given
        String key = "test-key";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-key?presigned=true";

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // When
        String result = storage.generatePresignedUrl(key, "");

        // Then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void getS3Client_returnsInjectedClient() {
        // When
        S3Client result = storage.getS3Client();

        // Then
        assertThat(result).isSameAs(s3Client);
    }
}