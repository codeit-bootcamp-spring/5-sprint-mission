package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = S3BinaryContentStorage.class)
@TestPropertySource(properties = {
        "discodeit.s3.bucket=test-bucket",
        "discodeit.s3.access-key=test-key",
        "discodeit.s3.secret-key=test-secret",
        "discodeit.s3.region=ap-northeast-2",
        "discodeit.s3.presigned-url-expiration=60",

        "AWS_S3_ACCESS_KEY_DEV=dummy-access-dev",
        "AWS_S3_SECRET_KEY_DEV=dummy-secret-dev",
        "AWS_S3_REGION_DEV=ap-northeast-2",
        "AWS_S3_BUCKET_DEV=dummy-bucket-dev",
        "AWS_S3_PRESIGNED_URL_EXPIRATION_DEV=60",
        "AWS_S3_ACCESS_KEY_PROD=dummy-access-prod",
        "AWS_S3_SECRET_KEY_PROD=dummy-secret-prod",
        "AWS_S3_REGION_PROD=ap-northeast-2",
        "AWS_S3_BUCKET_PROD=dummy-bucket-prod",
        "AWS_S3_PRESIGNED_URL_EXPIRATION_PROD=600"
})
public class S3BinaryContentStorageTest {

    @InjectMocks
    private S3BinaryContentStorage s3Storage;

    @Mock
    private S3Client mockS3Client;

    @Mock
    private S3Presigner mockS3Presigner;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPut_Success() {
        UUID testId = UUID.randomUUID();
        byte[] testBytes = "Hello S3".getBytes();

        when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().eTag("test-etag").build());

        UUID resultId = s3Storage.put(testId, testBytes);

        assertEquals(testId, resultId);
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testGet_Success() {
        UUID testId = UUID.randomUUID();

        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> mockResponseStream = (ResponseInputStream<GetObjectResponse>) mock(ResponseInputStream.class);

        when(mockS3Client.getObject(any(GetObjectRequest.class)))
            .thenReturn(mockResponseStream);

        InputStream resultStream = s3Storage.get(testId);

        assertNotNull(resultStream);
        assertEquals(mockResponseStream, resultStream);
        verify(mockS3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void testGeneratePresignedUrl_Success() throws Exception {
        UUID testId = UUID.randomUUID();
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-key?signature=mock";

        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(new URL(expectedUrl));

        when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
            .thenReturn(mockPresignedRequest);

        String url = s3Storage.generatePresignedUrl(testId.toString());

        assertNotNull(url);
        assertEquals(expectedUrl, url);
        verify(mockS3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }
}
